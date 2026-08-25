from pathlib import Path

ROOT = Path('.')


def read(path: str) -> str:
    return Path(path).read_text(encoding='utf-8')


def write(path: str, text: str) -> None:
    Path(path).write_text(text, encoding='utf-8')


def insert_before(path: str, marker: str, addition: str, unique_key: str) -> None:
    text = read(path)
    if unique_key in text:
        return
    if marker not in text:
        raise SystemExit(f'{path}: marker not found for insert_before: {marker!r}')
    text = text.replace(marker, addition.rstrip() + '\n\n' + marker, 1)
    write(path, text)


def insert_after(path: str, marker: str, addition: str, unique_key: str) -> None:
    text = read(path)
    if unique_key in text:
        return
    if marker not in text:
        raise SystemExit(f'{path}: marker not found for insert_after: {marker!r}')
    text = text.replace(marker, marker + '\n' + addition.rstrip() + '\n', 1)
    write(path, text)


def append_once(path: str, addition: str, unique_key: str) -> None:
    text = read(path)
    if unique_key in text:
        return
    write(path, text.rstrip() + '\n\n' + addition.rstrip() + '\n')


# DECISIONS — permanent workflow decision.
append_once(
    'docs/DECISIONS.md',
    '''## D-055 — Ana yöneticilik rolü local çalışan agent'a devredilebilir
**Durum:** Kabul edildi — 2026-08-26

- Kullanıcı ana manager/koordinatör rolünü local çalışan agent'a devretme kararı verdi.
- Manager rolünün sorumlulukları değişmez: PRE-STEP refresh, state consistency, Research/Coding/Test delegation, spec/decision ownership, independent acceptance, POST-STEP living-memory sync ve repo-wide stale-reference audit aynen korunur.
- GitHub/repo durable source of truth olmaya devam eder; local agent scratchpad'i veya sohbet hafızası canonical kararların yerine geçmez.
- Local manager takeover sırasında önce root `AGENTS.md`, `docs/LOCAL_MANAGER_HANDOFF.md`, `docs/START_HERE.md`, `docs/PROJECT_MEMORY_PROTOCOL.md` ve repo içindeki tüm Markdown dosyaları okunur.
- `docs/LOCAL_MANAGER_HANDOFF.md` bootstrap/transfer belgesidir; canonical specs ve `DECISIONS.md` yerine geçmez.
- Research/Coding/Test bağımsız rol ayrımı D-016 uyarınca korunur. Explicit independent Research AI gereksinimi local manager'ın kendi research'üyle ikame edilemez.
- Bu manager transition numaralı curriculum/architecture adımı değildir; **6B'yi yürütmez veya tamamlamaz**.
- Transition anındaki canonical execution state: `6A ✅ GNS-v0 / D-054`, `6B 🟡 active / not executed`.

Canonical takeover bootstrap: `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`.
''',
    '## D-055 — Ana yöneticilik rolü local çalışan agent\'a devredilebilir'
)

# AI agent workflow — make the manager implementation explicit.
insert_after(
    'docs/AI_AGENT_WORKFLOW.md',
    '### 1.1 Ana Yönetici / Ürün ve Mimari Koordinatörü\n',
    '''**D-055 local-manager clarification:** Ana yönetici rolü cloud/chat manager olmak zorunda değildir; local çalışan agent bu rolü devralabilir. Tool/terminal erişimi rolün authority contract'ını değiştirmez. Local manager da `AGENTS.md`, `docs/LOCAL_MANAGER_HANDOFF.md`, `PROJECT_MEMORY_PROTOCOL.md` ve bütün canonical specs'e bağlıdır. Manager transition tek başına numbered step execution değildir.''',
    'D-055 local-manager clarification'
)

# START_HERE — local manager should discover the handoff immediately.
insert_after(
    'docs/START_HERE.md',
    'Bu dosya proje başka bir ChatGPT sohbetine, coding agent\'a veya yeni bir çalışma oturumuna aktarılırken **ilk okunacak dosyadır**.\n',
    '''**Local manager takeover — D-055:** Repo yerel çalışan ana yönetici agent'a devrediliyorsa root `AGENTS.md` ve `docs/LOCAL_MANAGER_HANDOFF.md` bu dosyayla birlikte ilk bootstrap setidir. Local takeover sırasında repo içindeki tüm Markdown dosyaları ayrıca tamamen okunmalıdır.''',
    'Local manager takeover — D-055'
)
insert_before(
    'docs/START_HERE.md',
    '## 4. Güncel stage mapping',
    '''### D-055 — Local manager takeover
Ana manager/koordinatör rolü local çalışan agent'a devredilebilir. GitHub durable source of truth, D-024/D-027/D-050 PRE/POST protokolü ve Research/Coding/Test bağımsızlığı değişmez. Canonical takeover bootstrap: `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`. Bu transition 6B'yi yürütmez.''',
    '### D-055 — Local manager takeover'
)
# Add explicit takeover files to the reading-order block if not already present.
start = read('docs/START_HERE.md')
if '0. `AGENTS.md`' not in start:
    marker = '1. `docs/START_HERE.md`\n'
    if marker not in start:
        raise SystemExit('START_HERE reading-order marker missing')
    start = start.replace(marker, '0. `AGENTS.md`\n0a. `docs/LOCAL_MANAGER_HANDOFF.md`\n' + marker, 1)
    write('docs/START_HERE.md', start)

# HANDOFF_STATE — durable operational takeover note, state unchanged.
text = read('docs/HANDOFF_STATE.md')
if '- **D-055:**' not in text:
    marker = '- **D-054:** GNS-v0 Granularity & Naming Standard; 6A semantic decomposition/ID contract tamamlandı.\n'
    if marker not in text:
        raise SystemExit('HANDOFF_STATE D-054 marker missing')
    text = text.replace(marker, marker + '- **D-055:** Ana manager/koordinatör rolü local çalışan agent\'a devredilebilir; takeover bootstrap `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`; project contracts/state değişmez.\n', 1)
    write('docs/HANDOFF_STATE.md', text)
insert_before(
    'docs/HANDOFF_STATE.md',
    '## 10. Tamamlanan aşamalar',
    '''## 9.1 D-055 / Local manager transition

Kullanıcı ana yönetici rolünü local çalışan agent'a devretme kararı verdi.

- Local manager D-024/D-027/D-050 protokolüne aynen uyar.
- İlk takeover: `AGENTS.md` → `LOCAL_MANAGER_HANDOFF` → `START_HERE` → `PROJECT_MEMORY_PROTOCOL` → bütün Markdown repo audit/read.
- Research/Coding/Test role separation korunur.
- Bu transition numbered step değildir.
- Canonical execution **değişmedi**: 6A tamamlandı; 6B active/not-executed.
''',
    '## 9.1 D-055 / Local manager transition'
)

# PROJECT_CONTEXT — short durable snapshot of manager mode.
insert_before(
    'PROJECT_CONTEXT.md',
    '## 4. Güncel stage mapping',
    '''### D-055 — Local manager takeover
Ana manager/koordinatör rolü local çalışan agent'a devredilebilir. Canonical bootstrap `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`; GitHub durable source of truth ve D-024/D-027/D-050 PRE/POST workflow değişmez. Transition 6B'yi yürütmez; current execution hâlâ 6A ✅ / 6B 🟡 not-executed.''',
    '### D-055 — Local manager takeover'
)

# MASTER_PLAN — record workflow transition without changing numbered state.
text = read('docs/MASTER_PLAN.md')
if '- D-055:' not in text:
    marker = '- D-052: 5C final V1 foundation backbone `FBB-v0`; canonical file `docs/V1_FOUNDATION_BACKBONE.md`.\n'
    if marker not in text:
        raise SystemExit('MASTER_PLAN D-052 marker missing')
    text = text.replace(marker, marker + '- D-055: main manager role local çalışan agent\'a devredilebilir; canonical takeover bootstrap `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`; numbered execution state değişmez.\n', 1)
    write('docs/MASTER_PLAN.md', text)
insert_before(
    'docs/MASTER_PLAN.md',
    '# Güncel Konum',
    '''# Local Manager Transition — D-055

Bu operasyonel handoff numaralı stage değildir. Local manager mevcut accepted specs'i devralır; PRE/POST GitHub memory protocol, Research/Coding/Test separation ve acceptance discipline aynen sürer. Takeover'ın canonical bootstrap belgeleri `AGENTS.md` ve `docs/LOCAL_MANAGER_HANDOFF.md`'dir. Transition **6B execution değildir**.

---
''',
    '# Local Manager Transition — D-055'
)

# EXECUTION_INDEX — decision pointer only; no step status change.
text = read('docs/EXECUTION_INDEX.md')
if '- D-055:' not in text:
    marker = '- D-054: 6A final granularity/naming contract `GNS-v0`; semantic entity boundaries + stable logical ID rules.\n'
    if marker not in text:
        raise SystemExit('EXECUTION_INDEX D-054 marker missing')
    text = text.replace(marker, marker + '- D-055: local çalışan agent main manager rolünü devralabilir; workflow contracts değişmez; transition 6B execution değildir.\n', 1)
    write('docs/EXECUTION_INDEX.md', text)

# STEP_STATUS — operational note, table stays unchanged.
insert_before(
    'docs/STEP_STATUS.md',
    '## Repository memory hygiene — D-050',
    '''## Manager transition — D-055

Ana manager/koordinatör rolü local çalışan agent'a devredilebilir. Bu transition numbered step değildir ve current state'i değiştirmez: **6A ✅ / 6B 🟡 active-not-executed**. Bootstrap: `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`.
''',
    '## Manager transition — D-055'
)

# README — human navigation.
text = read('README.md')
if '- `AGENTS.md` — local manager/agent bootstrap' not in text:
    marker = '- `docs/START_HERE.md` — yeni sohbet için başlangıç ve okuma sırası\n'
    if marker not in text:
        raise SystemExit('README START_HERE marker missing')
    replacement = '- `AGENTS.md` — local manager/agent bootstrap ve non-negotiable workflow\n- `docs/LOCAL_MANAGER_HANDOFF.md` — local ana yönetici için kapsamlı takeover paketi\n' + marker
    text = text.replace(marker, replacement, 1)
    write('README.md', text)

# PROJECT_MASTER_CONTEXT — stable workflow clarification.
append_once(
    'docs/PROJECT_MASTER_CONTEXT.md',
    '''## D-055 / Local manager continuity guard

Ana manager/koordinatör rolü local çalışan agent'a taşınabilir; manager implementation değişikliği accepted product/learning/curriculum contracts'i değiştirmez. Local manager `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md` ile bootstrap olur, repo-wide Markdown takeover okuması yapar ve D-024/D-027/D-050 PRE/POST memory discipline'ine aynen uyar. GitHub durable source of truth olmaya devam eder; Research/Coding/Test bağımsızlığı korunur.
''',
    '## D-055 / Local manager continuity guard'
)

# PROJECT_MEMORY_PROTOCOL — clarify protocol applies to local manager too.
insert_after(
    'docs/PROJECT_MEMORY_PROTOCOL.md',
    '> **GitHub durable source of truth\'tur. Sohbet hafızası veya tek bir durum dosyası repo içindeki başka bir stale dosyanın varlığını mazur göstermez.**\n',
    '''**D-055 clarification:** Ana yönetici local çalışan agent olsa da bu protokol aynen bağlayıcıdır. Local manager takeover bootstrap'ı `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md` ile yapılır; manager implementation değişikliği PRE/POST kurallarını gevşetmez.''',
    'D-055 clarification'
)

# Progress log — transition history, not a numbered step.
append_once(
    'docs/PROGRESS_LOG.md',
    '''### 2026-08-26 — Local manager takeover paketi hazırlandı — D-055

- Kullanıcı ana yönetici/koordinatör rolünü local çalışan agent'a devretme kararı verdi.
- Root `AGENTS.md` local agent bootstrap'ı oluşturuldu.
- `docs/LOCAL_MANAGER_HANDOFF.md` ürün hedefi, accepted models, invariants, execution state, full stage plan, AŞAMA 6 handoff, agent workflow, memory protocol ve file-reading planını tek takeover paketinde topladı.
- D-055 permanent workflow decision olarak kaydedildi: manager implementation local olabilir; GitHub durable source, D-024/D-027/D-050 ve Research/Coding/Test separation değişmez.
- `START_HERE`, `HANDOFF_STATE`, `PROJECT_CONTEXT`, `MASTER_PLAN`, `EXECUTION_INDEX`, `STEP_STATUS`, `AI_AGENT_WORKFLOW`, `PROJECT_MASTER_CONTEXT`, `PROJECT_MEMORY_PROTOCOL` ve README local-manager pointer/guard'larıyla hizalandı.
- Bu operasyonel transition **numaralı 6B adımını yürütmedi**. Canonical state değişmedi: **6A ✅ GNS-v0 / D-054; 6B 🟡 active / not executed**.
- Local manager ilk gerçek numbered work öncesi repo içindeki tüm Markdown dosyalarını okuyacak ve 6B için ayrıca fresh PRE-STEP GitHub refresh yapacak.
''',
    '### 2026-08-26 — Local manager takeover paketi hazırlandı — D-055'
)

print('Local manager handoff sync applied.')
