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
| **5C — İlk 8–12 haftalık curriculum backbone** | ✅ | FBB-v0 / D-052. V1 foundation authoring-seed subgraph tamamlandı. |
| **5D — Graph architecture QA** | ✅ | GQA-v0 / D-053. Corrective seed patch sonrası architecture QA PASS. |
| **6A — Granularity + naming standardı** | ✅ | GNS-v0 / D-054. Semantic granularity + stable logical ID standardı tamamlandı. |
| **6B — Full-route decomposition blueprint** | 🟡 Aktif | 23 route family için ortak decomposition/authoring blueprint tasarlanacak. **Henüz yürütülmedi.** |
| **6C–20** | ⬜ Bekliyor | 6B sonrası canonical sırada. |

## Manager transition — D-055

Ana manager/koordinatör rolü local çalışan agent'a devredilebilir. Transition numbered step değildir ve current state'i değiştirmez: **6A ✅ / 6B 🟡 active-not-executed**. Bootstrap: `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`.

## Repository memory hygiene — D-050

2026-08-25 repo-wide documentation audit yapıldı. Bu bakım **numaralı bir curriculum/architecture adımı değildir**, dolayısıyla 5B'yi ilerletmedi.

Bağlayıcı değişiklik:
- her numaralı step sonunda living-memory seti istisnasız kontrol edilir,
- `PROJECT_CONTEXT.md` current snapshot olarak zorunlu sync kapsamındadır,
- `START_HERE`, `HANDOFF_STATE`, `STEP_STATUS`, `EXECUTION_INDEX`, `MASTER_PLAN`, `PROGRESS_LOG`, `DECISIONS` aynı kapanış turunda kontrol edilir,
- repo-wide stale step/stage/file/decision reference scan yapılır,
- stable specs volatile active-step kopyalamaz.

Canonical: `docs/PROJECT_MEMORY_PROTOCOL.md` / D-050.

## Son tamamlanan numaralı adım — 6A

**Final:** `GNS-v0 — Granularity & Naming Standard` / D-054.  
Ana çıktı: `docs/GRANULARITY_NAMING_STANDARD.md`.

6A sonucu:
- Domain/Module/Topic/Skill/Objective semantic sınırları kilitlendi,
- Skill granularity independent evidence/remediation/prerequisite/reuse temelli hale geldi,
- under/over-fragmentation guard'ları tanımlandı,
- shared vs language/tool/context-specific Skill split kriterleri tanımlandı,
- Objective atomicity/observable-action standardı tanımlandı,
- logical ID formatı stable/locale-independent/version-free yapıldı,
- display/localization/alias ile identity ayrıldı,
- FBB seed ratification/split/merge/normalize lifecycle'ı tanımlandı,
- 6B decomposition authoring handoff'u tanımlandı.

6A ayrı external Research AI kullanmadı; full coverage/current-industry/prerequisite independent Research QA 6H'de zorunlu kalır.

## Aktif adım — 6B Full-route decomposition blueprint

**6B henüz yürütülmedi.**

6B başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca fresh PRE-STEP GitHub refresh zorunludur.

6B'de özellikle:
- 23 route family için tek ortak decomposition row/template contract'ı,
- Domain→Module→Topic authoring blueprint'i,
- Skill/Objective candidate üretim akışı,
- duplicate resolver + shared Skill reuse akışı,
- source/provenance/freshness alanları,
- GNS-v0 reason-code/granularity review entegrasyonu,
- 6C–6F detailed-map paketlerinin ortak çıktı biçimi

kesinleştirilecek.