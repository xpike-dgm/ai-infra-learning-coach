# Execution Step Status

Bu dosya `docs/EXECUTION_INDEX.md` içindeki canonical adım kodlarının güncel durumunu hızlı takip etmek için tutulur.

## Durum anahtarı
- ✅ Tamamlandı
- 🟡 Aktif
- ⬜ Bekliyor
- 🔴 Bloke

## Güncel durum — 2026-08-28

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
| **8A — Bilgi mimarisi** | ✅ | UXIA-v0 / D-068. Today/Learn/Progress/Profile semantic shell + shared detail/focused-flow IA; 49/49 QA PASS. |
| **8B — Ana ekran** | ✅ | THUX-v0 / D-069. Action-first Today hierarchy + current PlannedTask queue + capacity/reason/empty/degraded semantics; 90/90 QA PASS. |
| **8C — Günlük çalışma akışı** | ✅ | TRUX-v0 / D-070. Focused daily working flow + Task Runner choreography + shared focused-flow frame; 123/123 QA PASS. |
| **8D — Sınav UX** | ✅ | ASUX-v0 / D-071. Tek assessment session interior + atomic boundary submission + semantic result; 107/107 QA PASS. |
| **8E — Skill/progress/weakness UX** | ✅ | SPWX-v0 / D-072. Tek 8-state Skill vokabüleri + kilitli Topic etiketleri + inventory-only counting; 128/128 QA PASS. |
| **8F — Tasarım sistemi** | ✅ | VDSX-v0 / D-073. Expression layer + 6 tone + 46/46 state eşlemesi + WCAG çapaları; 121/121 QA PASS. |
| **8G — Wireframe/prototip** | 🟡 Aktif | Concrete wireframe + prototype geometry + ölçülmüş palet; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |
| **9–20** | ⬜ Bekliyor | 8G sonrası canonical sırada. |

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

## Son tamamlanan numaralı adım — 8F

**Final:** `VDSX-v0 — Visual Design System` / D-073.  
**Ana çıktı:** `docs/DESIGN_SYSTEM_SPEC.md` + `ux/8f_design_system/`.

8F sonucu:
- design system bir expression layer'dır; canonical state'in iddia etmediği anlam, severity, aciliyet veya hiyerarşi ekleyemez (`visual_severity <= canonical_severity`),
- tam 6 tone: `neutral`, `active`, `positive_confirmed`, `attention`, `pending_unresolved`, `system_fault`; tone anlamdan atanır, histen değil,
- 8A–8E'nin 46 surface state'i, 8 Skill state'i, 6 Topic state'i ve 4 qualifier'ı eksiksiz eşlendi; eksik veya uydurulmuş state yok,
- `system_fault` yalnız `error_recoverable` ve `data_recovery_required`'a izinlidir; hiçbir learning state alarm tonu alamaz,
- attention grubunda görünmek tone yükseltmez; `confirmed_review_due` ve Topic `weakening` bilinçle `neutral`,
- `stopped_no_penalty`, `resume_invalidated`, `slot_recomposed`, `capacity_zero`, `empty_no_evidence_yet` için non-negative tone zorunlu,
- typography 8 role + technical içerik için `mono`; scalable units, %200 metin desteği, state label body'den önce truncate olmaz,
- Türkçe casing korundu: locale-naive case transform yasak, kilitli etiketler yazıldığı gibi render edilir, zorunlu all-caps yok,
- renk semantic role olarak belirtilir; WCAG 1.4.3/1.4.11 eşikleri, tema başına ölçüm, dark inversiyon değil, renk asla tek taşıyıcı değil,
- 4dp ritim + `4/8/12/16/24/32/48`; en az 48dp dokunma hedefi; focused-flow exit tam hedefi korur,
- ikonlar destekleyicidir ve state ikonu daima metin etiketiyle görünür,
- motion'ın ikna edici rolü yok; countdown, task-completion ödül animasyonu, decay ve streak animasyonu yasak; reduced-motion bilgi kaybettirmez,
- 18 component kabul edilmiş yüzeylere ve sahibi spec'lere eşlendi; hiçbiri surface/state icat edemez,
- progress-bar yalnız bounded factual konum için; competence, career, oran ve level için yasak; gauge/dial/level meter/rank/tier/streak/heatmap/leaderboard/trend-line yasak,
- somut hex paleti kilitlenmedi; token role'leri, tone eşlemeleri ve kontrast kısıtları kilitlendi, palet 8G/10'da ölçülerek üretilir,
- independent validator **121/121 PASS**; tone kapsamı doğrudan 8A–8E yaml kontratlarından hesaplanan state union'ına karşı doğrulanır ve mutation test uygulandı. Stage 6 + Stage 7 + 8A–8E + external-memory regressions PASS.

## Aktif adım — 8G Wireframe/prototip

**8G henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
