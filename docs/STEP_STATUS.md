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
| **8F — Tasarım sistemi** | 🟡 Aktif | Typography/color/spacing/iconography/motion/component library; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |
| **8G–20** | ⬜ Bekliyor | 8F sonrası canonical sırada. |

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

## Son tamamlanan numaralı adım — 8E

**Final:** `SPWX-v0 — Progress, Skill State & Weakness UX` / D-072.  
**Ana çıktı:** `docs/PROGRESS_SKILL_UX_SPEC.md` + `ux/8e_progress_skill_weakness/`.

8E sonucu:
- Progress canonical evidence state'in projection'ıdır; mastery engine, score, competence yüzdesi, career tracker veya streak dashboard değildir,
- `TEPM-v0`nin 8 derived presentation state'i ve precedence'ı bütün Skill'lere genelleştirildi; technical Skill'ler için ikinci vokabüler üretilmedi ve TEPM-v0 değişmedi,
- 8 Türkçe Skill etiketi ve 6 Türkçe Topic etiketi kilitlendi; internal state ID'leri sabit kaldı,
- `at_risk` attention qualifier'dır; dokuzuncu primary state değildir ve sessizce düşürülmez,
- multi-axis truth sıralanır fakat çökertilmez; `skill_detail` mastery/retention/prerequisite/weakness eksenlerini ayrı ayrı incelenebilir tutar,
- Topic state derived orchestration'dır; prerequisite iddiası değildir, Skill ortalaması değildir ve yüzdesi yoktur,
- progress overview `demonstrated_capability_inventory` + `attention_set` yarılarından oluşur ve planner priority üretmez,
- Progress sayabilir fakat puanlayamaz; count yalnız etiketli inventory'dir ve competence ima etmek için total'e bölünmez,
- yalnız `supported` ve `confirmed` weakness gösterilir; AI hypothesis confirmed gibi sunulamaz; localization yukarı/aşağı yayılmaz,
- `remediation_task_completed != remediation_closed`; closure fresh/H0/direct/verified/prerequisite-valid evidence ister,
- `technical_english_profile` TEPM-v0 semantiğini değiştirmeden sunar; general/official CEFR, certification ve numeric aggregate yasak,
- `learning_history` streak calendar/contribution graph değildir; attendance başarı sayılmaz,
- `assessment_report` longitudinal'dir, mastery sahibi değildir ve session'ları score/grade/trend line hâline getiremez,
- `review_due` nötr ve non-demoting; `verification_due` history silmez; görsel severity canonical state'i aşamaz,
- 10 Progress semantic state / 14 forbidden anti-pattern,
- independent validator **128/128 PASS**; state seti doğrudan TEPM policy, TSM, WLRM ve 8D session.yaml ile çapraz doğrulandı ve mutation test uygulandı. Stage 6 + Stage 7 + 8A + 8B + 8C + 8D + external-memory regressions PASS.

## Aktif adım — 8F Tasarım sistemi

**8F henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
