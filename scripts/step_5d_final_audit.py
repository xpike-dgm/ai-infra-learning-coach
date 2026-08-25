from pathlib import Path

root = Path('.')
living = [
    Path('PROJECT_CONTEXT.md'),
    Path('docs/START_HERE.md'),
    Path('docs/HANDOFF_STATE.md'),
    Path('docs/STEP_STATUS.md'),
    Path('docs/EXECUTION_INDEX.md'),
    Path('docs/MASTER_PLAN.md'),
]

for p in living:
    s = p.read_text(encoding='utf-8')
    if '6A' not in s:
        raise SystemExit(f'{p}: missing 6A current state')
    for stale in ['5D henüz yürütülmedi', '5D — Graph architecture QA — **AKTİF**', '**Aktif:** **`5D', '**Aktif:** `5D']:
        if stale in s:
            raise SystemExit(f'{p}: stale current-state marker: {stale}')

pc = Path('PROJECT_CONTEXT.md').read_text(encoding='utf-8')
if '## 7. Curriculum backbone / knowledge graph — AŞAMA 5 tamamlandı' not in pc:
    raise SystemExit('PROJECT_CONTEXT: AŞAMA 5 completion heading missing')

start = Path('docs/START_HERE.md').read_text(encoding='utf-8')
if 'docs/GRAPH_ARCHITECTURE_QA.md' not in start or 'D-053' not in start:
    raise SystemExit('START_HERE: GQA/D-053 bootstrap pointer missing')

fbb = Path('docs/V1_FOUNDATION_BACKBONE.md').read_text(encoding='utf-8')
if '--soft/supporting-->' in fbb:
    raise SystemExit('FBB: invalid prerequisite reason_kind soft/supporting remains')
if '# 11.1 Explicit TopicSkillLink seed matrix — 5D corrective patch' not in fbb:
    raise SystemExit('FBB: TopicSkillLink corrective matrix missing')

qa = Path('docs/GRAPH_ARCHITECTURE_QA.md').read_text(encoding='utf-8')
if 'GQA-v0' not in qa or 'D-053' not in qa or 'AŞAMA 5 tamamlanabilir' not in qa:
    raise SystemExit('GQA spec incomplete')

dec = Path('docs/DECISIONS.md').read_text(encoding='utf-8')
if '## D-053 — Foundation graph architecture QA = GQA-v0' not in dec:
    raise SystemExit('DECISIONS: D-053 missing')

# Repo-wide report. Historical occurrences are printed for classification, not blindly failed.
terms = ['5D henüz yürütülmedi', '5D — Graph architecture QA', 'soft/supporting', 'D-053', '6A — Granularity + naming standardı']
for term in terms:
    hits=[]
    for p in root.rglob('*.md'):
        try: text=p.read_text(encoding='utf-8')
        except Exception: continue
        for i,line in enumerate(text.splitlines(),1):
            if term in line:
                hits.append(f'{p}:{i}: {line.strip()}')
    print(f'\n[{term}] hits={len(hits)}')
    for h in hits[:100]: print(h)

print('\n5D FINAL REPOSITORY CONSISTENCY AUDIT: PASS')
