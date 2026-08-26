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

# START_HERE reading order should include completed 6D/6E summaries and datasets.
p = ROOT / 'docs/START_HERE.md'
s = p.read_text(encoding='utf-8')
needle = '11f. `curriculum/decomposition/6c_foundations/manifest.yaml`\n11g. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`'
if needle in s:
    s = s.replace(needle, '11f. `curriculum/decomposition/6c_foundations/manifest.yaml`\n11g. `docs/SYSTEMS_DETAILED_MAP.md`\n11h. `curriculum/decomposition/6d_systems/manifest.yaml`\n11i. `docs/GPU_ML_INFERENCE_DETAILED_MAP.md`\n11j. `curriculum/decomposition/6e_gpu_ml_inference/manifest.yaml`\n11k. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`')
p.write_text(s, encoding='utf-8')

print('EXTRA_POST_SYNC=PASS')
