# Execution Step Status

Bu dosya `docs/EXECUTION_INDEX.md` içindeki canonical adım kodlarının güncel durumunu hızlı takip etmek için tutulur.

## Durum anahtarı
- ✅ Tamamlandı
- 🟡 Aktif
- ⬜ Bekliyor
- 🔴 Bloke

## Güncel durum — 2026-08-25

| Adım | Durum | Açıklama |
|---|---|---|
| **AŞAMA 1 — Ürün Çerçevesi** | ✅ | `1A–1D` tamamlandı. |
| **AŞAMA 2 — Öğrenme ve Mastery Modeli** | ✅ | `2A–2F` tamamlandı. GRE-v0 + RVR-v0 canonical. |
| **AŞAMA 3 — Adaptif Günlük Planlama Motoru** | ✅ | `3A–3H` tamamlandı. 16/16 scenario + 20/20 invariant PASS. |
| **AŞAMA 4 — Assessment sistemi** | ✅ | `4A–4E` tamamlandı: DMA-v0, WBA-v0, MCA-v0, QAB-v0, AIV-v0. |
| **5A — Ana domain haritası** | ✅ | PDM-v0 / D-049. 23 route family + domain roles + high-level authoring relations. |
| **5B — Graph / Topic metadata sözleşmesi** | 🟡 Aktif | Domain/Module/Topic/Skill/Objective graph schema ve metadata contract tasarlanacak. **Henüz yürütülmedi.** |
| **5C–20** | ⬜ Bekliyor | 5B sonrası canonical sırada. |

## Repository memory hygiene — D-050

2026-08-25 repo-wide documentation audit yapıldı. Bu bakım **numaralı bir curriculum/architecture adımı değildir**, dolayısıyla 5B'yi ilerletmedi.

Bağlayıcı değişiklik:
- her numaralı step sonunda living-memory seti istisnasız kontrol edilir,
- `PROJECT_CONTEXT.md` current snapshot olarak zorunlu sync kapsamındadır,
- `START_HERE`, `HANDOFF_STATE`, `STEP_STATUS`, `EXECUTION_INDEX`, `MASTER_PLAN`, `PROGRESS_LOG`, `DECISIONS` aynı kapanış turunda kontrol edilir,
- repo-wide stale step/stage/file/decision reference scan yapılır,
- stable specs volatile active-step kopyalamaz.

Canonical: `docs/PROJECT_MEMORY_PROTOCOL.md` / D-050.

## Son tamamlanan numaralı adım — 5A

**Final:** `PDM-v0 — Professional Domain Backbone` / D-049.  
Ana çıktı: `docs/CURRICULUM_DOMAIN_MAP.md`.

5A kararları:
- Technical English parallel track.
- Python + C + Linux/Git/Shell complementary early foundations.
- DS&A supporting common foundation.
- Systems core: Modern C++ + Architecture + OS/Memory + Concurrency + Networking.
- Distributed/platform: Distributed Systems + Storage/DB + Containers/Cloud/Observability.
- Performance cross-cutting core.
- Accelerator core: GPU Architecture → CUDA/Triton.
- ML/Transformer supporting depth; generic ML research specialization değil.
- LLM Inference → serving systems → KV/batching/scheduling/quantization bağlı family'ler.
- Multi-GPU/NCCL/RDMA distributed+network+GPU convergence.
- AI/GPU Infrastructure target integration domain.
- Open Source/engineering practice/projects/capstones route boyunca artan professional evidence layer.
- Security/reliability/math/numerical ihtiyaçları hidden prerequisite bırakılmayacak.
- Tool/vendor adı stable concept'in yerine geçmeyecek.
- Domain-level ilişkiler authoring guidance; runtime hard prerequisite Skill→Skill PRG-v0.

## Aktif adım — 5B Graph / Topic metadata sözleşmesi

**5B henüz yürütülmedi.**

5B başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP GitHub refresh zorunludur.

5B'de özellikle:
- Domain/Module/Topic/Skill/Learning Objective entity contract,
- placement vs canonical Skill identity,
- many-to-many Topic↔Skill,
- Skill→Skill hard/soft prerequisites,
- required/critical/optional metadata,
- evidence contract/profile refs,
- retention/remediation/diagnostic flags,
- cross-domain reuse,
- professional/project/capstone attribution,
- source/version/freshness,
- curriculum versioning/migration,
- graph validation/indexing/performance handoff

kesinleştirilecek.