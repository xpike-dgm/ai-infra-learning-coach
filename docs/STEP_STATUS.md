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
| **5D — Graph architecture QA** | 🟡 Aktif | FBB-v0 cycle/dead-end/hidden prerequisite/duplicate/reachability açısından doğrulanacak. **Henüz yürütülmedi.** |
| **6A–20** | ⬜ Bekliyor | 5D sonrası canonical sırada. |

## Repository memory hygiene — D-050

2026-08-25 repo-wide documentation audit yapıldı. Bu bakım **numaralı bir curriculum/architecture adımı değildir**, dolayısıyla 5B'yi ilerletmedi.

Bağlayıcı değişiklik:
- her numaralı step sonunda living-memory seti istisnasız kontrol edilir,
- `PROJECT_CONTEXT.md` current snapshot olarak zorunlu sync kapsamındadır,
- `START_HERE`, `HANDOFF_STATE`, `STEP_STATUS`, `EXECUTION_INDEX`, `MASTER_PLAN`, `PROGRESS_LOG`, `DECISIONS` aynı kapanış turunda kontrol edilir,
- repo-wide stale step/stage/file/decision reference scan yapılır,
- stable specs volatile active-step kopyalamaz.

Canonical: `docs/PROJECT_MEMORY_PROTOCOL.md` / D-050.

## Son tamamlanan numaralı adım — 5C

**Final:** `FBB-v0 — V1 Foundation Backbone` / D-052.  
Ana çıktı: `docs/V1_FOUNDATION_BACKBONE.md`.

5C kararları:
- 8–12 hafta takvim değil scope-equivalent content envelope,
- zero-entry Computer/Programming bridge yeni broad career Domain'i yaratmadan early topics'e yerleştirildi,
- Python + C + Linux/Git/Shell + early DS&A + Technical English başlangıç subgraph'ı tanımlandı,
- Technical English day-one parallel fakat global technical hard gate değil,
- KGC-v0 uyumlu Skill/Objective authoring-seed skeleton tanımlandı,
- 6A/6C ratification öncesi lifecycle `authoring_seed / not_learner_published`,
- shared programming mental model ile language-specific production capability ayrıldı,
- initial Skill→Skill hard/soft prerequisite edges PRG-v0 semantics ile tanımlandı,
- evidence/retention/diagnostic/remediation anchor'ları GRE/QAB/RVR canonical davranışına bağlandı,
- AŞAMA 15 production-content handoff'u ve 5D QA fixture'ları tanımlandı.

Ayrı Research AI kullanılmadı; external full-route coverage/current-industry/prerequisite Research QA 6H'de zorunlu kalır.

## Aktif adım — 5D Graph architecture QA

**5D henüz yürütülmedi.**

5D başlamadan `docs/PROJECT_MEMORY_PROTOCOL.md` uyarınca fresh PRE-STEP GitHub refresh zorunludur.

5D'de özellikle:
- hard prerequisite DAG / cycle kontrolü,
- inaccessible required Skill / dead-end kontrolü,
- hidden prerequisite ve task interpretability riski,
- duplicate semantic Skill / Topic reuse kontrolü,
- English global-gate ihlali kontrolü,
- branch isolation / independent continuation,
- FBB-v0 Objective/Topic reachability,
- KGC version/migration uyumu,
- AŞAMA 6 ve AŞAMA 15 genişleme handoff güvenliği

doğrulanacak.
