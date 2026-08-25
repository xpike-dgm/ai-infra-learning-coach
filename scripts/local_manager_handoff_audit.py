from pathlib import Path
import re

ROOT = Path('.')


def text(path):
    return Path(path).read_text(encoding='utf-8')

errors = []

def require(cond, msg):
    if not cond:
        errors.append(msg)

# Inventory.
md = sorted(
    p.as_posix() for p in ROOT.rglob('*.md')
    if '.git' not in p.parts
)
print(f'Markdown inventory: {len(md)} files')
for p in md:
    print(' -', p)
require(len(md) == 55, f'expected 55 Markdown files after takeover package, found {len(md)}')

required_files = [
    'AGENTS.md',
    'PROJECT_CONTEXT.md',
    'README.md',
    'docs/LOCAL_MANAGER_HANDOFF.md',
    'docs/START_HERE.md',
    'docs/PROJECT_MEMORY_PROTOCOL.md',
    'docs/HANDOFF_STATE.md',
    'docs/EXECUTION_INDEX.md',
    'docs/STEP_STATUS.md',
    'docs/DECISIONS.md',
    'docs/MASTER_PLAN.md',
]
for p in required_files:
    require(Path(p).exists(), f'missing required takeover/current-state file: {p}')

handoff = text('docs/LOCAL_MANAGER_HANDOFF.md')
agents = text('AGENTS.md')

# The handoff must point to every Markdown resource so the local manager cannot silently miss a file.
missing_from_handoff = [p for p in md if p not in handoff]
require(not missing_from_handoff, f'Markdown paths missing from LOCAL_MANAGER_HANDOFF: {missing_from_handoff}')

# Takeover safety markers.
for needle in [
    'repo içindeki tüm Markdown dosyaları',
    '6B 🟡 ACTIVE / NOT EXECUTED',
    '6B — Full-route decomposition blueprint',
    'fresh PRE-STEP GitHub refresh',
    'D-043 = **GERİ ÇEKİLDİ / NONCANONICAL**',
    'D-055',
    'Research/Coding/Test',
]:
    require(needle in handoff, f'LOCAL_MANAGER_HANDOFF missing marker: {needle}')

for needle in [
    'docs/LOCAL_MANAGER_HANDOFF.md',
    'tüm Markdown dosyalarının envanterini çıkar ve tamamını oku',
    '6B henüz yürütülmedi',
    'PROJECT_MEMORY_PROTOCOL.md',
]:
    require(needle in agents, f'AGENTS.md missing marker: {needle}')

# Decision coverage.
decisions = text('docs/DECISIONS.md')
ids = re.findall(r'^## (D-\d{3})\b', decisions, flags=re.M)
unique_ids = sorted(set(ids))
print('Decision IDs:', unique_ids[0] if unique_ids else None, '...', unique_ids[-1] if unique_ids else None, 'count=', len(unique_ids))
require(len(unique_ids) == 55, f'expected D-001..D-055 decision entries, found {len(unique_ids)} unique IDs')
require(all(f'D-{i:03d}' in unique_ids for i in range(1, 56)), 'decision sequence D-001..D-055 has a gap')
require('## D-043 — Standalone specialization-track aşaması' in decisions and 'GERİ ÇEKİLDİ / YANLIŞ YORUM' in decisions, 'D-043 withdrawn guard missing')
require('## D-055 — Ana yöneticilik rolü local çalışan agent' in decisions, 'D-055 missing in DECISIONS')

# D-055 durable pointers.
pointer_files = [
    'docs/AI_AGENT_WORKFLOW.md',
    'docs/START_HERE.md',
    'docs/HANDOFF_STATE.md',
    'PROJECT_CONTEXT.md',
    'docs/MASTER_PLAN.md',
    'docs/EXECUTION_INDEX.md',
    'docs/STEP_STATUS.md',
    'docs/PROJECT_MASTER_CONTEXT.md',
    'docs/PROJECT_MEMORY_PROTOCOL.md',
    'docs/PROGRESS_LOG.md',
]
for p in pointer_files:
    require('D-055' in text(p), f'{p}: missing D-055 local-manager pointer')
require('docs/LOCAL_MANAGER_HANDOFF.md' in text('README.md'), 'README missing local handoff pointer')
require('AGENTS.md' in text('README.md'), 'README missing AGENTS pointer')

# Canonical current execution must remain unchanged by manager transition.
living = {
    'PROJECT_CONTEXT.md': ['6A ✅ GNS-v0 / D-054', '6B 🟡 Full-route decomposition blueprint', 'HENÜZ YÜRÜTÜLMEDİ'],
    'docs/START_HERE.md': ['6A ✅ GNS-v0 / D-054', '6B 🟡 Full-route decomposition blueprint', '6B henüz yürütülmedi'],
    'docs/HANDOFF_STATE.md': ['6A ✅ GNS-v0 / D-054', '6B 🟡 Full-route decomposition blueprint', '6B henüz yürütülmedi'],
    'docs/STEP_STATUS.md': ['6A — Granularity + naming standardı', 'GNS-v0 / D-054', '6B — Full-route decomposition blueprint', 'Henüz yürütülmedi'],
    'docs/EXECUTION_INDEX.md': ['[x] **6A — Granularity + naming standardı**', '[ ] **6B — Full-route decomposition blueprint** **AKTİF**'],
    'docs/MASTER_PLAN.md': ['[x] 6A — Granularity + naming standardı — GNS-v0 / D-054', '[ ] 6B — Full-route decomposition blueprint — **AKTİF**'],
}
for p, needles in living.items():
    s = text(p)
    for n in needles:
        require(n in s, f'{p}: missing current execution marker {n!r}')

stale_patterns = [
    '6A 🟡 Granularity + naming standardı',
    '6A henüz yürütülmedi',
    '[ ] **6A — Granularity + naming standardı** **AKTİF**',
    '[x] **6B — Full-route decomposition blueprint**',
    '[x] 6B — Full-route decomposition blueprint',
    '6C 🟡',
]
for p in ['PROJECT_CONTEXT.md','docs/START_HERE.md','docs/HANDOFF_STATE.md','docs/STEP_STATUS.md','docs/EXECUTION_INDEX.md','docs/MASTER_PLAN.md']:
    s = text(p)
    for pat in stale_patterns:
        require(pat not in s, f'{p}: stale/incorrect current-state marker: {pat}')

# Historical/noncanonical guards.
require('HISTORICAL / SUPERSEDED' in text('docs/LEARNING_ENGINE.md'), 'LEARNING_ENGINE historical/superseded guard missing')
require('NON-CANONICAL' in text('docs/ENGLISH_TRACK.md'), 'ENGLISH_TRACK noncanonical guard missing')
require(not Path('docs/TODO.md').exists(), 'obsolete docs/TODO.md unexpectedly exists')

# Mandatory protocol still has the full post-step set and local-manager clarification.
protocol = text('docs/PROJECT_MEMORY_PROTOCOL.md')
for n in [
    'docs/EXECUTION_INDEX.md',
    'docs/STEP_STATUS.md',
    'docs/HANDOFF_STATE.md',
    'docs/PROGRESS_LOG.md',
    'docs/MASTER_PLAN.md',
    'PROJECT_CONTEXT.md',
    'docs/START_HERE.md',
    'docs/DECISIONS.md',
    'repo-wide stale-reference',
    'D-055 clarification',
]:
    require(n in protocol, f'PROJECT_MEMORY_PROTOCOL missing required marker: {n}')

# Handoff should include every future stage 7–20 and every 6B–6H code.
for code in ['6B','6C','6D','6E','6F','6G','6H'] + [str(i) for i in range(7,21)]:
    require(code in handoff, f'LOCAL_MANAGER_HANDOFF missing future-stage/code marker: {code}')

if errors:
    print('\nLOCAL MANAGER HANDOFF AUDIT: FAIL')
    for e in errors:
        print('ERROR:', e)
    raise SystemExit(1)

print('\nLOCAL MANAGER HANDOFF AUDIT: PASS')
print('Canonical state preserved: 6A complete / 6B active-not-executed.')
print('D-055 durable manager transition present; all Markdown paths represented in handoff.')
