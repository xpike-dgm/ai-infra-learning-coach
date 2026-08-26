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

# START_HERE reading order should include completed 6D/6E summaries and datasets.
p = ROOT / 'docs/START_HERE.md'
s = p.read_text(encoding='utf-8')
needle = '11f. `curriculum/decomposition/6c_foundations/manifest.yaml`\n11g. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`'
if needle in s:
    s = s.replace(needle, '11f. `curriculum/decomposition/6c_foundations/manifest.yaml`\n11g. `docs/SYSTEMS_DETAILED_MAP.md`\n11h. `curriculum/decomposition/6d_systems/manifest.yaml`\n11i. `docs/GPU_ML_INFERENCE_DETAILED_MAP.md`\n11j. `curriculum/decomposition/6e_gpu_ml_inference/manifest.yaml`\n11k. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`')
p.write_text(s, encoding='utf-8')

print('EXTRA_POST_SYNC=PASS')
