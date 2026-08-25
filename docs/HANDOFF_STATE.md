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

## 6. Tamamlanan aşamalar

- AŞAMA 1 ✅
- AŞAMA 2 ✅ — GRE-v0 / RVR-v0
- AŞAMA 3 ✅ — adaptive planner; 16/16 scenarios, 20/20 invariants PASS
- AŞAMA 4 ✅ — DMA-v0 / WBA-v0 / MCA-v0 / QAB-v0 / AIV-v0
- AŞAMA 5 devam ediyor:
  - 5A ✅ PDM-v0 / D-049
  - 5B 🟡 Graph / Topic metadata sözleşmesi — aktif, henüz yürütülmedi
  - 5C–5D ⬜
- AŞAMA 6–20 ⬜

## 7. Güncel kesin konum

**Aktif:** `5B — Graph / Topic metadata sözleşmesi`  
**5B henüz yürütülmedi.**

## 8. 5B'de kesinleştirilecekler

Ana soru:
> PDM-v0 domain backbone'u, AŞAMA 6'nın yüzlerce/binlerce granular capability node'una güvenli biçimde genişleyebilecek versioned knowledge graph contract'ına nasıl dönüştürülmeli?

Kesinleştirilecek:
- Domain / Module / Topic / Skill / Learning Objective entity contract,
- curriculum placement ile canonical Skill identity ayrımı,
- Topic↔Skill many-to-many,
- Skill→Skill hard/soft prerequisite edges,
- 5A domain-level authoring relations,
- required / critical / optional semantics,
- Objective evidence profile / direct evidence requirements,
- assessment/QAB refs,
- retention/remediation/diagnostic metadata,
- Technical English prerequisite metadata,
- professional capability / project / capstone attribution,
- cross-domain shared Skill reuse,
- source/provenance/version/freshness,
- curriculum graph version + migration semantics,
- indexing/bounded traversal/performance requirements,
- 5C V1 backbone ve 6A–6H granular map handoff.

## 9. 5B için PRE-STEP doğrudan okunacaklar
1. `docs/HANDOFF_STATE.md`
2. `docs/EXECUTION_INDEX.md`
3. `docs/STEP_STATUS.md`
4. `docs/DECISIONS.md`
5. `docs/MASTER_PLAN.md`
6. `PROJECT_CONTEXT.md`
7. `docs/CURRICULUM_DOMAIN_MAP.md`
8. `docs/LEARNING_ENGINE_SPEC.md`
9. `docs/PREREQUISITE_POLICY_SPEC.md`
10. `docs/MASTERY_SIGNALS_SPEC.md`
11. `docs/MASTERY_FORMULA_V0.md`
12. `docs/RETENTION_FORGETTING_SPEC.md`
13. `docs/QUESTION_BANK_SPEC.md`
14. `docs/AI_GENERATED_RESOURCE_VALIDATION_SPEC.md`
15. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`
16. `docs/ENGLISH_FOUNDATION_RULES.md`
17. `docs/PROJECT_MEMORY_PROTOCOL.md`

5B başlamadan fresh PRE-STEP GitHub refresh zorunludur.