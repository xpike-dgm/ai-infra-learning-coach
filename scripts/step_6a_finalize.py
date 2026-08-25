from pathlib import Path

ROOT = Path('.')

living = [
    Path('PROJECT_CONTEXT.md'),
    Path('docs/START_HERE.md'),
    Path('docs/HANDOFF_STATE.md'),
    Path('docs/STEP_STATUS.md'),
    Path('docs/EXECUTION_INDEX.md'),
    Path('docs/MASTER_PLAN.md'),
    Path('docs/PROGRESS_LOG.md'),
    Path('docs/DECISIONS.md'),
]

for p in living:
    if not p.exists():
        raise SystemExit(f'missing mandatory living file: {p}')

spec = Path('docs/GRANULARITY_NAMING_STANDARD.md').read_text(encoding='utf-8')
for needle in ['GNS-v0', 'D-054', '**Durum:** TAMAMLANDI', '6B — Full-route decomposition blueprint']:
    if needle not in spec:
        raise SystemExit(f'GNS spec missing: {needle}')

required_current = {
    'PROJECT_CONTEXT.md': ['6A ✅ GNS-v0 / D-054', '6B 🟡 Full-route decomposition blueprint', "6B başlamadan fresh PRE-STEP GitHub refresh zorunludur"],
    'docs/START_HERE.md': ['6A ✅ GNS-v0 / D-054', '6B 🟡 Full-route decomposition blueprint', '6B henüz yürütülmedi'],
    'docs/HANDOFF_STATE.md': ['D-054', '6A ✅ GNS-v0 / D-054', '6B 🟡 Full-route decomposition blueprint', '6B henüz yürütülmedi'],
    'docs/STEP_STATUS.md': ['GNS-v0 / D-054', '6B — Full-route decomposition blueprint', 'Henüz yürütülmedi'],
    'docs/EXECUTION_INDEX.md': ['D-054: 6A final granularity/naming contract', '[x] **6A — Granularity + naming standardı**', '[ ] **6B — Full-route decomposition blueprint** **AKTİF**'],
    'docs/MASTER_PLAN.md': ['[x] 6A — Granularity + naming standardı — GNS-v0 / D-054', '[ ] 6B — Full-route decomposition blueprint — **AKTİF**'],
    'docs/PROGRESS_LOG.md': ['6A Granularity + Naming Standard tamamlandı', 'GNS-v0 — Granularity & Naming Standard', '6B — Full-route decomposition blueprint'],
    'docs/DECISIONS.md': ['D-054 — Granularity & Naming Standard = GNS-v0'],
}

for path, needles in required_current.items():
    text = Path(path).read_text(encoding='utf-8')
    for needle in needles:
        if needle not in text:
            raise SystemExit(f'{path}: missing current-state marker: {needle}')

# Current living docs must not claim 6A is still active/not executed.
stale_living_patterns = [
    '6A 🟡 Granularity + naming standardı',
    '6A henüz yürütülmedi',
    '6A — Granularity + naming standardı — **AKTİF**',
    '[ ] **6A — Granularity + naming standardı** **AKTİF**',
]
for p in living:
    text = p.read_text(encoding='utf-8')
    for pat in stale_living_patterns:
        if pat in text:
            raise SystemExit(f'{p}: stale current 6A marker: {pat}')

# Long-lived docs that should know the accepted 6A contract.
for p in [
    Path('README.md'),
    Path('docs/PROJECT_MASTER_CONTEXT.md'),
    Path('docs/GRANULAR_CAPABILITY_MAP_PLAN.md'),
    Path('docs/CURRICULUM.md'),
]:
    text = p.read_text(encoding='utf-8')
    if 'GRANULARITY_NAMING_STANDARD.md' not in text or 'GNS-v0' not in text:
        raise SystemExit(f'{p}: missing GNS-v0 canonical pointer')

# Record that the final audit caught and repaired START_HERE stage-map drift.
log = Path('docs/PROGRESS_LOG.md')
log_text = log.read_text(encoding='utf-8')
marker = '6A final consistency re-audit — START_HERE stage-map drift düzeltildi'
if marker not in log_text:
    log_text = log_text.rstrip() + f'''\n\n**6A final consistency re-audit — START_HERE stage-map drift düzeltildi**\n- D-050 final verification sırasında `docs/START_HERE.md` içindeki üst `Güncel stage mapping` bölümünün D-054 eklenmiş olmasına rağmen eski `6A 🟡 / 6B–6H ⬜` satırlarını taşıdığı fark edildi.\n- Aynı dosyanın alt current-state bölümü zaten `6A ✅ / 6B 🟡` gösteriyordu; üst mapping de `6A ✅ GNS-v0 / D-054`, `6B 🟡`, `6C–6H ⬜` olarak düzeltildi.\n- Mandatory living-state dosyaları tekrar doğrulandı; canonical execution **6A tamamlandı / 6B aktif-henüz-yürütülmedi** olarak tutarlı.\n- Bu düzeltme yeni numaralı adım değildir ve 6B'yi yürütmez.\n'''
    log.write_text(log_text, encoding='utf-8')

# Re-read progress after append and ensure current markers survive.
log_text = log.read_text(encoding='utf-8')
if marker not in log_text or '6B — Full-route decomposition blueprint' not in log_text:
    raise SystemExit('progress-log finalization failed')

# Repo inventory and informational stale scan. Historical specs/logs may legitimately say
# that 6A was future/not-yet at the time, so only current living docs are blocking above.
markdown = sorted([p for p in ROOT.rglob('*.md') if '.git' not in p.parts])
print(f'Markdown inventory: {len(markdown)} files')
for pattern in ['6A henüz yürütülmedi', '6A 🟡', 'D-054', 'GNS-v0', '6B henüz yürütülmedi']:
    hits = []
    for p in markdown:
        for i, line in enumerate(p.read_text(encoding='utf-8').splitlines(), 1):
            if pattern in line:
                hits.append(f'{p}:{i}: {line.strip()}')
    print(f'\n[{pattern}] hits={len(hits)}')
    for hit in hits[:80]:
        print(hit)

print('\n6A FINAL D-050 CONSISTENCY AUDIT: PASS')
