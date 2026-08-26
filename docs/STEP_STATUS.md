# Execution Step Status

Bu dosya `docs/EXECUTION_INDEX.md` içindeki canonical adım kodlarının güncel durumunu hızlı takip etmek için tutulur.

## Durum anahtarı
- ✅ Tamamlandı
- 🟡 Aktif
- ⬜ Bekliyor
- 🔴 Bloke

## Güncel durum — 2026-08-27

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
| **6B — Full-route decomposition blueprint** | ✅ | FRDB-v0 / D-056. Ortak machine-readable authoring package + QA contract tamamlandı. |
| **6C — Foundations detailed map** | ✅ | FDM-v0 / D-057. D01–D05 package + FBB 41/47 mapping + internal graph QA tamamlandı. |
| **6D — Systems detailed map** | ✅ | SDM-v0 / D-058. D06–D13 package + 6C cross-package reuse + birleşik hard-graph QA tamamlandı. |
| **6E — GPU / ML / Inference detailed map** | ✅ | GIM-v0 / D-059. D14–D22 package + prior registry reuse + combined hard-graph QA tamamlandı. |
| **6F — Professional engineering / project map** | ✅ | PEM-v0 / D-060. D23 professional workflow + OSS + project/capstone map tamamlandı. |
| **6G — Weakness localization + remediation mapping** | ✅ | WLRM-v0 / D-061. 543 Skill + 590 Objective exact weakness/remediation overlay ve 590 route tamamlandı. |
| **6H — Coverage / prerequisite / Research QA** | 🟡 Aktif | AŞAMA 6 bağımsız external coverage/current-industry/hidden-prerequisite QA. **Henüz yürütülmedi.** |
| **7–20** | ⬜ Bekliyor | 6H sonrası canonical sırada. |

## Manager transition — D-055

Ana manager/koordinatör rolü local çalışan agent'a devredilebilir. Transition numbered step değildir; subsequent numbered execution canonical state'i normal biçimde ilerletir. Bootstrap: `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`.

## Repository memory hygiene — D-050

2026-08-25 repo-wide documentation audit yapıldı. Bu bakım **numaralı bir curriculum/architecture adımı değildir**, dolayısıyla 5B'yi ilerletmedi.

Bağlayıcı değişiklik:
- her numaralı step sonunda living-memory seti istisnasız kontrol edilir,
- `PROJECT_CONTEXT.md` current snapshot olarak zorunlu sync kapsamındadır,
- `START_HERE`, `HANDOFF_STATE`, `STEP_STATUS`, `EXECUTION_INDEX`, `MASTER_PLAN`, `PROGRESS_LOG`, `DECISIONS` aynı kapanış turunda kontrol edilir,
- repo-wide stale step/stage/file/decision reference scan yapılır,
- stable specs volatile active-step kopyalamaz.

Canonical: `docs/PROJECT_MEMORY_PROTOCOL.md` / D-050.

## Son tamamlanan numaralı adım — 6G

**Final:** `WLRM-v0 — Weakness Localization & Remediation Map` / D-061.  
**Ana çıktı:** `docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md` + `curriculum/decomposition/6g_weakness_remediation/`.

6G sonucu:
- 543 accepted Skill / 590 accepted Objective exact coverage,
- 590 Objective-specific remediation route,
- 12 failure-attribution rule / 15 remediation strategy,
- invalid/prerequisite-contaminated false-negative guard,
- assisted/provisional hypothesis-only guard,
- post-mastery `verification_due` hysteresis,
- broad Topic/Domain/project broadcast guard,
- remediation closure only by fresh valid evidence,
- 0 blocking / 4 non-blocking 6G review,
- independent external Research QA 6H'ye pending.

## Aktif adım — 6H Coverage / prerequisite / Research QA

**6H henüz yürütülmedi.** Fresh PRE-STEP + ayrı bağımsız Research AI zorunludur.
