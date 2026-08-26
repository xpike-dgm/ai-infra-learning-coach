from pathlib import Path
import re
import yaml

ROOT = Path(__file__).resolve().parents[1]

living = [
    'PROJECT_CONTEXT.md',
    'docs/START_HERE.md',
    'docs/HANDOFF_STATE.md',
    'docs/EXECUTION_INDEX.md',
    'docs/STEP_STATUS.md',
    'docs/MASTER_PLAN.md',
    'docs/DECISIONS.md',
    'docs/PROGRESS_LOG.md',
    'vault/agent/CURRENT_CONTEXT.md',
    'vault/agent/OPEN_LOOPS.md',
    'vault/wiki/sources/Execution State Source.md',
    'vault/wiki/projects/AI Infra Learning Coach Delivery.md',
]
historical_living = {'docs/PROGRESS_LOG.md'}

for rel in living:
    if not (ROOT / rel).exists():
        raise SystemExit(f'POST_AUDIT_FAIL missing living file: {rel}')

required = {
    'PROJECT_CONTEXT.md': ['D-059', 'GIM-v0', '6F 🟡'],
    'docs/START_HERE.md': ['D-059', 'GIM-v0', '6F 🟡'],
    'docs/HANDOFF_STATE.md': ['GIM-v0 / D-059', '**Aktif:** `6F'],
    'docs/EXECUTION_INDEX.md': ['D-059', '[x] **6E', '**6F — Professional engineering / project map** **AKTİF**'],
    'docs/STEP_STATUS.md': ['GIM-v0 / D-059', 'Aktif adım — 6F'],
    'docs/MASTER_PLAN.md': ['D-059', '### [x] 6E', '### [ ] 6F — Professional engineering / project map — **AKTİF**'],
    'docs/DECISIONS.md': ['## D-059 — GPU / ML / Inference detailed map = GIM-v0'],
    'docs/PROGRESS_LOG.md': ['### 2026-08-26 — 6E GPU / ML / Inference Detailed Map tamamlandı'],
    'vault/agent/CURRENT_CONTEXT.md': ['GIM-v0', 'Aktif adım **6F'],
    'vault/agent/OPEN_LOOPS.md': ['[x] 6E GPU/ML/Inference detailed map', '6F Professional Engineering detailed map **AKTİF**'],
    'vault/wiki/sources/Execution State Source.md': ['6E GIM-v0 / D-059 tamamlandı', 'aktif adım 6F'],
    'vault/wiki/projects/AI Infra Learning Coach Delivery.md': ['next_action: "6F Professional engineering / project map için fresh PRE-STEP"', 'GPU/ML/Inference output:'],
}

for rel, markers in required.items():
    text = (ROOT / rel).read_text(encoding='utf-8')
    for marker in markers:
        if marker not in text:
            raise SystemExit(f'POST_AUDIT_FAIL {rel} missing required marker: {marker}')

forbidden_living = [
    '6E henüz yürütülmedi',
    '6E başlamadan fresh PRE-STEP',
    '6E başlamadan yeni PRE-STEP',
    'Aktif adım **6E',
    '**Aktif:** **`6E',
    '6E 🟡 GPU / ML / Inference detailed map',
    'Sıradaki numaralı çalışma 6E',
]
for rel in living:
    if rel in historical_living:
        continue
    text = (ROOT / rel).read_text(encoding='utf-8')
    for phrase in forbidden_living:
        if phrase in text:
            raise SystemExit(f'POST_AUDIT_FAIL stale living marker in {rel}: {phrase}')

summary = ROOT / 'docs/GPU_ML_INFERENCE_DETAILED_MAP.md'
if not summary.exists():
    raise SystemExit('POST_AUDIT_FAIL canonical 6E summary missing')
summary_text = summary.read_text(encoding='utf-8')
for marker in ['GIM-v0', 'D-059', '143 Skill', '247 hard / 32 soft', '467/467']:
    if marker not in summary_text:
        raise SystemExit(f'POST_AUDIT_FAIL summary missing: {marker}')

pkg = ROOT / 'curriculum/decomposition/6e_gpu_ml_inference'
expected_files = {
    'manifest.yaml','sources.yaml','organization_entities.yaml','skills.yaml','objectives.yaml',
    'topic_skill_links.yaml','prerequisite_edges.yaml','capability_requirements.yaml',
    'professional_attributions.yaml','project_capstone_attributions.yaml','seed_mappings.yaml',
    'review_queue.yaml','qa_report.yaml'
}
actual = {p.name for p in pkg.glob('*.yaml')}
if actual != expected_files:
    raise SystemExit(f'POST_AUDIT_FAIL 6E YAML collection mismatch missing={sorted(expected_files-actual)} extra={sorted(actual-expected_files)}')
qa = yaml.safe_load((pkg/'qa_report.yaml').read_text(encoding='utf-8'))
counts = qa['counts']
expected_counts = {
    'domains':9,'modules':27,'topics':70,'skills':143,'objectives':159,'topic_skill_links':230,
    'prerequisite_edges':279,'hard_prerequisite_edges':247,'soft_prerequisite_edges':32,
    'reused_prior_package_skills':59,'reused_6c_skills':9,'reused_6d_skills':50,
    'open_non_blocking_reviews':4,'open_blocking_reviews':0,
}
for k,v in expected_counts.items():
    if counts.get(k) != v:
        raise SystemExit(f'POST_AUDIT_FAIL qa count {k}: {counts.get(k)} != {v}')
if qa.get('result') != 'PASS_WITH_OPEN_NON_BLOCKING_REVIEWS':
    raise SystemExit('POST_AUDIT_FAIL QA result drift')

reviews6d = yaml.safe_load((ROOT/'curriculum/decomposition/6d_systems/review_queue.yaml').read_text(encoding='utf-8'))
row = next((x for x in reviews6d if x.get('review_id') == 'review.6d.accelerator_forward_reuse'), None)
if not row or row.get('status') != 'resolved':
    raise SystemExit('POST_AUDIT_FAIL review.6d.accelerator_forward_reuse not resolved')
sdm_summary = (ROOT/'docs/SYSTEMS_DETAILED_MAP.md').read_text(encoding='utf-8')
if '| Açık non-blocking review | 3 |' not in sdm_summary:
    raise SystemExit('POST_AUDIT_FAIL SYSTEMS_DETAILED_MAP open review count not synced to 3')

historical_prefixes = ('docs/PROGRESS_LOG.md','vault/agent/session-logs/')
repo_hits = []
patterns = [
    re.compile(r'6E[^\n]{0,80}(aktif|AKTİF)[^\n]{0,80}(henüz yürütülmedi|not executed)', re.I),
    re.compile(r'(aktif adım|current active step)[^\n]{0,40}6E', re.I),
    re.compile(r'6E başlamadan[^\n]{0,80}PRE-STEP', re.I),
]
for p in ROOT.rglob('*'):
    if not p.is_file() or p.suffix.lower() not in {'.md','.yaml','.yml','.py','.txt'}:
        continue
    rel = p.relative_to(ROOT).as_posix()
    text = p.read_text(encoding='utf-8', errors='replace')
    for pattern in patterns:
        for m in pattern.finditer(text):
            repo_hits.append((rel, m.group(0).replace('\n',' ')))

blocking = [(rel,hit) for rel,hit in repo_hits if not rel.startswith(historical_prefixes)]
print(f'STALE_SCAN_TOTAL_HITS={len(repo_hits)}')
for rel, hit in repo_hits:
    cls = 'HISTORICAL_ALLOWED' if rel.startswith(historical_prefixes) else 'BLOCKING'
    print(f'{cls}: {rel}: {hit}')
if blocking:
    raise SystemExit(f'POST_AUDIT_FAIL repo-wide stale active-state hits={len(blocking)}')

print('POST_STEP_AUDIT=PASS')
