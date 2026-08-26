from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]

def patch(path, old, new, required=True):
    p = ROOT / path
    s = p.read_text(encoding='utf-8')
    if old not in s:
        if required:
            raise SystemExit(f'EXTRA_SYNC_FAIL missing marker in {path}: {old[:100]}')
        return
    p.write_text(s.replace(old, new), encoding='utf-8')

patch('docs/SYSTEMS_DETAILED_MAP.md', '| Açık non-blocking review | 4 |', '| Açık non-blocking review | 3 |')
patch('docs/SYSTEMS_DETAILED_MAP.md', '0 blocking / 4 non-blocking', '0 blocking / 3 non-blocking', required=False)
patch('docs/STEP_STATUS.md', '0 blocking / 4 non-blocking review', '0 blocking / 3 non-blocking review', required=False)

# 6D summary's forward handoff is now historical/resolved by completed 6E.
patch(
    'docs/SYSTEMS_DETAILED_MAP.md',
    "- Systems/performance/concurrency/networking Skills'ini clone'lamaz; prerequisite veya TopicSkillLink olarak reuse eder,\n- stable GPU/inference concept'leri ile tool/version-specific capability'leri ayırır,\n- math/numerical hidden prerequisite'leri explicit candidate Skills/edges olarak yakalar,\n- aynı FRDB-v0 collection ve QA contract'ını uygular,\n- `review.6d.accelerator_forward_reuse` kaydını tüketir,\n- 6E başlamadan fresh PRE-STEP GitHub refresh yapar.\n\n**6D sonrası numaralı adım:** `6E — GPU / ML / Inference detailed map`.",
    "6E GIM-v0 / D-059 bu forward handoff'u tamamladı:\n- Systems/performance/concurrency/networking Skills clone'lanmadan canonical ID ile reuse edildi,\n- stable GPU/inference concepts ile tool/version-specific capabilities ayrıldı,\n- math/numerical hidden prerequisites explicit Skills/edges olarak yakalandı,\n- `review.6d.accelerator_forward_reuse` resolved edildi.\n\n**Güncel sonraki numaralı adım:** `6F — Professional engineering / project map`; `review.6d.professional_overlay_reconciliation` 6F'ye açık kalır."
)

# EXECUTION_INDEX has a compact current-location footer in addition to the AŞAMA 6 table.
patch(
    'docs/EXECUTION_INDEX.md',
    '**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6D`\n**Aktif:** **`6E — GPU / ML / Inference detailed map`**\n\n6D SDM-v0 / D-058 ile tamamlandı. 6E henüz yürütülmedi; 6E başlamadan fresh PRE-STEP GitHub refresh zorunludur.',
    '**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6E`\n**Aktif:** **`6F — Professional engineering / project map`**\n\n6E GIM-v0 / D-059 ile tamamlandı. 6F henüz yürütülmedi; 6F başlamadan fresh PRE-STEP GitHub refresh zorunludur.'
)

# MASTER_PLAN also carries a compact current-location footer.
patch(
    'docs/MASTER_PLAN.md',
    '**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6D`\n**Aktif:** **`6E — GPU / ML / Inference detailed map`**\n\nBir sonraki yürütme: **6E başlamadan fresh PRE-STEP GitHub refresh → FRDB-v0 + FDM-v0 + SDM-v0 registry ile GPU/ML/Inference detailed map → POST-STEP D-050 sync + stale-reference audit.**',
    '**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6E`\n**Aktif:** **`6F — Professional engineering / project map`**\n\nBir sonraki yürütme: **6F başlamadan fresh PRE-STEP GitHub refresh → FRDB-v0 + FDM-v0 + SDM-v0 + GIM-v0 registry/attribution setleri ile Professional Engineering detailed map → POST-STEP D-050 sync + stale-reference audit.**'
)
patch('docs/MASTER_PLAN.md', '**Son senkron:** 2026-08-25', '**Son senkron:** 2026-08-26', required=False)

# Root bootstrap must never point a new manager at the completed step.
patch(
    'AGENTS.md',
    '- AŞAMA 6D: ✅ `SDM-v0 / D-058` tamamlandı.\n- **Aktif adım: 6E — GPU / ML / Inference detailed map.**\n- **6E henüz yürütülmedi.**\n- 6F–6H ve AŞAMA 7–20 bekliyor.\n\n**6E\'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 6E için ayrıca fresh PRE-STEP refresh yap.',
    '- AŞAMA 6D: ✅ `SDM-v0 / D-058` tamamlandı.\n- AŞAMA 6E: ✅ `GIM-v0 / D-059` tamamlandı.\n- **Aktif adım: 6F — Professional engineering / project map.**\n- **6F henüz yürütülmedi.**\n- 6G–6H ve AŞAMA 7–20 bekliyor.\n\n**6F\'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 6F için ayrıca fresh PRE-STEP refresh yap.'
)

# Local-manager takeover instructions must reflect the same durable state.
patch('docs/LOCAL_MANAGER_HANDOFF.md', 'Current execution state\'in hâlâ `6A–6D completed / 6E active-not-executed` olduğunu doğrula.', 'Current execution state\'in `6A–6E completed / 6F active-not-executed` olduğunu doğrula.')
patch('docs/LOCAL_MANAGER_HANDOFF.md', 'Ancak bundan sonra, 6E için **ayrı bir fresh PRE-STEP GitHub refresh** yap.', 'Ancak bundan sonra, 6F için **ayrı bir fresh PRE-STEP GitHub refresh** yap.')
patch(
    'docs/LOCAL_MANAGER_HANDOFF.md',
    '**Son tamamlanan numaralı adım:** `6D — Systems detailed map`\n**Final:** `SDM-v0 — Systems Detailed Map` / D-058\n**Canonical:** `docs/SYSTEMS_DETAILED_MAP.md` + `curriculum/decomposition/6d_systems/`\n\n**Aktif adım:** `6E — GPU / ML / Inference detailed map`\n**Durum:** **HENÜZ YÜRÜTÜLMEDİ**\n\nKullanıcı onaylı numbered work 6D\'yi tamamladı. Bu handoff belgesi **6E\'yi başlatmaz veya ilerletmez**.\n\nKullanıcı 6E\'yi devam ettirmek/onaylamak istediğinde:',
    '**Son tamamlanan numaralı adım:** `6E — GPU / ML / Inference detailed map`\n**Final:** `GIM-v0 — GPU / ML / Inference Detailed Map` / D-059\n**Canonical:** `docs/GPU_ML_INFERENCE_DETAILED_MAP.md` + `curriculum/decomposition/6e_gpu_ml_inference/`\n\n**Aktif adım:** `6F — Professional engineering / project map`\n**Durum:** **HENÜZ YÜRÜTÜLMEDİ**\n\nKullanıcı onaylı numbered work 6E\'yi tamamladı. Bu handoff belgesi **6F\'yi başlatmaz veya ilerletmez**.\n\nKullanıcı 6F\'yi devam ettirmek/onaylamak istediğinde:'
)
patch('docs/LOCAL_MANAGER_HANDOFF.md', 'fresh 6E PRE-STEP GitHub refresh\n→ 6E execution', 'fresh 6F PRE-STEP GitHub refresh\n→ 6F execution', required=False)
patch(
    'docs/LOCAL_MANAGER_HANDOFF.md',
    'Bu takeover sırasında 6E\'yi yürütme. Önce bana ürün hedefini, tamamlanan modelleri, değiştirilemez invariants\'ı, current exact step\'i, tamamlanan FDM-v0 ile SDM-v0\'ı ve sıradaki 6E scope\'unu özetleyip devralmaya hazır olduğunu söyle.',
    'Bu takeover sırasında 6F\'yi yürütme. Önce bana ürün hedefini, tamamlanan modelleri, değiştirilemez invariants\'ı, current exact step\'i, tamamlanan FDM-v0, SDM-v0 ve GIM-v0\'ı ve sıradaki 6F scope\'unu özetleyip devralmaya hazır olduğunu söyle.'
)
patch(
    'docs/LOCAL_MANAGER_HANDOFF.md',
    'AŞAMA 6D ✅ SDM-v0 / D-058\nAŞAMA 6E 🟡 ACTIVE — NOT EXECUTED\nAŞAMA 6F–6H ⬜',
    'AŞAMA 6D ✅ SDM-v0 / D-058\nAŞAMA 6E ✅ GIM-v0 / D-059\nAŞAMA 6F 🟡 ACTIVE — NOT EXECUTED\nAŞAMA 6G–6H ⬜'
)

# START_HERE reading order should include completed 6D/6E summaries and datasets.
p = ROOT / 'docs/START_HERE.md'
s = p.read_text(encoding='utf-8')
needle = '11f. `curriculum/decomposition/6c_foundations/manifest.yaml`\n11g. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`'
if needle in s:
    s = s.replace(needle, '11f. `curriculum/decomposition/6c_foundations/manifest.yaml`\n11g. `docs/SYSTEMS_DETAILED_MAP.md`\n11h. `curriculum/decomposition/6d_systems/manifest.yaml`\n11i. `docs/GPU_ML_INFERENCE_DETAILED_MAP.md`\n11j. `curriculum/decomposition/6e_gpu_ml_inference/manifest.yaml`\n11k. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`')
p.write_text(s, encoding='utf-8')

print('EXTRA_POST_SYNC=PASS')
