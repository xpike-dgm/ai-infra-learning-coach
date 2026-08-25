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
| **6A — Granularity + naming standardı** | 🟡 Aktif | AŞAMA 6 naming/granularity contract tasarlanacak. **Henüz yürütülmedi.** |
| **6B–20** | ⬜ Bekliyor | 6A sonrası canonical sırada. |

## Repository memory hygiene — D-050

2026-08-25 repo-wide documentation audit yapıldı. Bu bakım **numaralı bir curriculum/architecture adımı değildir**, dolayısıyla 5B'yi ilerletmedi.

Bağlayıcı değişiklik:
- her numaralı step sonunda living-memory seti istisnasız kontrol edilir,
- `PROJECT_CONTEXT.md` current snapshot olarak zorunlu sync kapsamındadır,
- `START_HERE`, `HANDOFF_STATE`, `STEP_STATUS`, `EXECUTION_INDEX`, `MASTER_PLAN`, `PROGRESS_LOG`, `DECISIONS` aynı kapanış turunda kontrol edilir,
- repo-wide stale step/stage/file/decision reference scan yapılır,
- stable specs volatile active-step kopyalamaz.

Canonical: `docs/PROJECT_MEMORY_PROTOCOL.md` / D-050.

## Son tamamlanan numaralı adım — 5D

**Final:** `GQA-v0 — Foundation Graph Architecture QA` / D-053.  
Ana çıktı: `docs/GRAPH_ARCHITECTURE_QA.md`.

5D sonucu:
- initial FBB-v0 iki blocking structural issue ile başladı,
- explicit TopicSkillLink seed matrix eklendi,
- invalid `reason_kind=supporting` canonical KGC reason kinds'e normalize edildi,
- target evidence'ı contaminate eden hidden prerequisite boşlukları minimal hard edges ile düzeltildi,
- hard graph DAG; self/dangling/conflicting edge yok,
- branch isolation, English global-gate guard, duplicate/reuse, required reachability ve 5C→6/15 handoff fixtures PASS,
- FBB hâlâ authoring_seed/not-learner-published; 6A/6C + 6H öncesi production publish yok.

5D ayrı external Research AI kullanmadı; full coverage/current-industry/prerequisite independent Research QA 6H'de zorunlu kalır.

## Aktif adım — 6A Granularity + naming standardı

**6A henüz yürütülmedi.**

6A başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca fresh PRE-STEP GitHub refresh zorunludur.

6A'da özellikle:
- Domain/Module/Topic/Skill/Objective granularity sınırları,
- canonical logical ID convention,
- stable identity vs display label,
- under/over-fragmentation guard,
- language-specific vs shared capability split kriteri,
- Objective atomization/observable-action standardı,
- FBB authoring_seed ratification/refactor kuralları,
- version/migration naming invariants

kesinleştirilecek.
