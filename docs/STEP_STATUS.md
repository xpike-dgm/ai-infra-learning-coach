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
| **5B — Graph / Topic metadata sözleşmesi** | ✅ | KGC-v0 / D-051. Versioned curriculum knowledge graph contract tamamlandı. |
| **5C — İlk 8–12 haftalık curriculum backbone** | 🟡 Aktif | KGC-v0 üzerinde V1 başlangıç alt graph iskeleti kurulacak. **Henüz yürütülmedi.** |
| **5D–20** | ⬜ Bekliyor | 5C sonrası canonical sırada. |

## Repository memory hygiene — D-050

2026-08-25 repo-wide documentation audit yapıldı. Bu bakım **numaralı bir curriculum/architecture adımı değildir**, dolayısıyla 5B'yi ilerletmedi.

Bağlayıcı değişiklik:
- her numaralı step sonunda living-memory seti istisnasız kontrol edilir,
- `PROJECT_CONTEXT.md` current snapshot olarak zorunlu sync kapsamındadır,
- `START_HERE`, `HANDOFF_STATE`, `STEP_STATUS`, `EXECUTION_INDEX`, `MASTER_PLAN`, `PROGRESS_LOG`, `DECISIONS` aynı kapanış turunda kontrol edilir,
- repo-wide stale step/stage/file/decision reference scan yapılır,
- stable specs volatile active-step kopyalamaz.

Canonical: `docs/PROJECT_MEMORY_PROTOCOL.md` / D-050.

## Son tamamlanan numaralı adım — 5B

**Final:** `KGC-v0 — Versioned Curriculum Knowledge Graph Contract` / D-051.  
Ana çıktı: `docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md`.

5B kararları:
- organization layer `Domain → Module → Topic`; capability/evidence layer `Skill → Learning Objective`,
- Skill canonical identity; Topic↔Skill many-to-many placement,
- Objective exactly one canonical Skill,
- Skill→Skill hard/soft prerequisite edges PRG-v0 ile aynı semantics,
- scope-relative required/critical/optional capability requirements,
- GRE Objective evidence profile, QAB refs, RVR retention metadata,
- learner-specific weakness state ile static remediation metadata ayrımı,
- Technical English hidden-prerequisite guard,
- professional/project/capstone granular attribution,
- provenance/freshness + immutable entity/graph versions,
- conservative split/merge/refactor migration,
- bounded/indexed traversal/performance contract.

5B ayrı Research AI kullanmadı; existing accepted specs'i internal graph contract'a formalize etti. Full external coverage/current-industry/prerequisite Research QA 6H'de zorunlu kalır.

## Aktif adım — 5C İlk 8–12 haftalık curriculum backbone

**5C henüz yürütülmedi.**

5C başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca yeni PRE-STEP GitHub refresh zorunludur.

5C'de özellikle:
- KGC-v0 ile V1 başlangıç subgraph'ı,
- Computer/Programming Foundations giriş köprüsü,
- Python + C + Linux/Git/Shell + gerekli early DS&A/English capability placements,
- canonical Skill/Objective IDs,
- initial hard/soft prerequisite edges,
- required/critical Objective profiles,
- V1 assessment/retention/diagnostic metadata anchors,
- first 8–12 week authoring scope sınırı,
- AŞAMA 15 production-content handoff'u

kesinleştirilecek.
