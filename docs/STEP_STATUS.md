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
| **6G — Weakness localization + remediation mapping** | ✅ | WLRM-v0 / D-061. Final 6H-patched registry için 549 Skill + 608 Objective exact weakness/remediation coverage. |
| **6H — Coverage / prerequisite / Research QA** | ✅ | S6ERQA-v0 / D-062. 3 bağımsız evaluator reconcile edildi; 6 stable Skill + freshness/evidence patch; 549/549 hard DAG; 10/10 review resolved. |
| **7A — İngilizce başlangıç ölçümü** | ✅ | EED-v0 / D-063. 15 Skill / 15 Objective / 16 hard edge diagnostic profile + 15 task family; QA PASS. |
| **7B — A1/A2/B1/B2+ teknik hedefleri** | ✅ | TECP-v0 / D-064. 15 Skill = 5 A1 + 5 A2 + 5 B1; 4 bounded B2+ extensions; 16/16 band-monotonic hard edges; QA PASS. |
| **7C — Günlük English bileşeni** | ✅ | DECP-v0 / D-065. Common capacity + daily candidate opportunity + PBR balance/starvation + state-driven task mix; QA PASS. |
| **7D — Teknik entegrasyon** | ✅ | TEIP-v0 / D-066. 4 construct-aware integration mode + component attribution + bidirectional contamination/scaffold guards; QA PASS. |
| **7E — English mastery** | ✅ | TEPM-v0 / D-067. 8 derived Skill state + qualified A1/A2/B1 Technical English profile + B2+ per-capability evidence; QA PASS. |
| **8A — Bilgi mimarisi** | 🟡 Aktif | UX information architecture; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |
| **8B–20** | ⬜ Bekliyor | 8A sonrası canonical sırada. |

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

## Son tamamlanan numaralı adım — 7E

**Final:** `TEPM-v0 — Technical English Mastery Profile` / D-067.  
**Ana çıktı:** `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC.md` + `curriculum/english/7e_mastery_profile/`.

7E sonucu:
- exact D01 15 Skill unchanged; no graph/mastery-engine mutation,
- 8 deterministic learner-facing derived Skill presentation state,
- qualified A1/A2/B1 Technical English base profile; uneven exact Skill detail preserved,
- `review_due` no demotion; `verification_due` uncertainty without instant deletion,
- confirmed remediation current profile'ı real evidence ile recompute eder; historical confirmation provenance korunur,
- no numeric English %, CEFR average, general-English/official/certification overclaim,
- B2+ only per-capability professional extension evidence on exact 4 TECP Skills,
- assisted/provisional/contaminated evidence cannot create independent-confirmed state,
- TEIP-v0 component attribution and contamination guards preserved,
- 18/18 safety fixture and 54/54 independent validator check PASS,
- Stage 6 + accepted 7B + 7C + 7D regressions PASS.

## AŞAMA 7 — tamamlandı

7A EED-v0 / D-063 → 7B TECP-v0 / D-064 → 7C DECP-v0 / D-065 → 7D TEIP-v0 / D-066 → 7E TEPM-v0 / D-067.

## Aktif adım — 8A Bilgi mimarisi

**8A henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
