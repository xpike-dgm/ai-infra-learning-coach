from pathlib import Path


def r(p): return Path(p).read_text(encoding='utf-8')
def w(p, s): Path(p).write_text(s, encoding='utf-8')

def before(p, marker, add, key):
    s = r(p)
    if key in s: return
    if marker not in s: raise SystemExit(f'{p}: missing marker {marker!r}')
    w(p, s.replace(marker, add.rstrip() + '\n\n' + marker, 1))

def after(p, marker, add, key):
    s = r(p)
    if key in s: return
    if marker not in s: raise SystemExit(f'{p}: missing marker {marker!r}')
    w(p, s.replace(marker, marker + '\n' + add.rstrip() + '\n', 1))

def append(p, add, key):
    s = r(p)
    if key in s: return
    w(p, s.rstrip() + '\n\n' + add.rstrip() + '\n')

# Permanent decision.
append('docs/DECISIONS.md', '''## D-055 — Ana yöneticilik rolü local çalışan agent'a devredilebilir
**Durum:** Kabul edildi — 2026-08-26

- Kullanıcı ana manager/koordinatör rolünü local çalışan agent'a devretme kararı verdi.
- Manager sorumlulukları değişmez: PRE-STEP refresh, state consistency, Research/Coding/Test delegation, spec/decision ownership, independent acceptance, POST-STEP living-memory sync ve repo-wide stale-reference audit.
- GitHub/repo durable source of truth olmaya devam eder; local scratchpad veya sohbet hafızası canonical kararların yerine geçmez.
- Local takeover bootstrap: root `AGENTS.md`, `docs/LOCAL_MANAGER_HANDOFF.md`, `docs/START_HERE.md`, `docs/PROJECT_MEMORY_PROTOCOL.md`, ardından repo içindeki tüm Markdown dosyalarının tam okunması.
- `LOCAL_MANAGER_HANDOFF.md` canonical specs/DECISIONS yerine geçmez; kayıpsız bootstrap ve navigation belgesidir.
- D-016 Research/Coding/Test bağımsızlığı korunur; explicit independent Research AI zorunluluğu manager'ın kendi araştırmasıyla ikame edilemez.
- Bu transition numbered curriculum/architecture adımı değildir; **6B'yi yürütmez veya tamamlamaz**.
- Transition state: `6A ✅ GNS-v0 / D-054`, `6B 🟡 active / not executed`.

Canonical takeover bootstrap: `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`.
''', '## D-055 — Ana yöneticilik rolü local çalışan agent\'a devredilebilir')

# Agent workflow clarification.
after('docs/AI_AGENT_WORKFLOW.md', '### 1.1 Ana Yönetici / Ürün ve Mimari Koordinatörü\n', '''**D-055 local-manager clarification:** Ana yönetici cloud/chat manager olmak zorunda değildir; local çalışan agent bu rolü devralabilir. Terminal/tool erişimi authority contract'ını değiştirmez. Local manager da `AGENTS.md`, `docs/LOCAL_MANAGER_HANDOFF.md`, `docs/PROJECT_MEMORY_PROTOCOL.md` ve bütün canonical specs'e bağlıdır. Manager transition tek başına numbered step execution değildir.''', 'D-055 local-manager clarification')

# START_HERE bootstrap.
after('docs/START_HERE.md', "Bu dosya proje başka bir ChatGPT sohbetine, coding agent'a veya yeni bir çalışma oturumuna aktarılırken **ilk okunacak dosyadır**.\n", '''**Local manager takeover — D-055:** Repo yerel çalışan ana yönetici agent'a devrediliyorsa root `AGENTS.md` ve `docs/LOCAL_MANAGER_HANDOFF.md` bu dosyayla birlikte ilk bootstrap setidir. Local takeover sırasında repo içindeki tüm Markdown dosyaları ayrıca tamamen okunmalıdır.''', 'Local manager takeover — D-055')
before('docs/START_HERE.md', '## 4. Güncel stage mapping', '''### D-055 — Local manager takeover
Ana manager/koordinatör rolü local çalışan agent'a devredilebilir. GitHub durable source of truth, D-024/D-027/D-050 PRE/POST protokolü ve Research/Coding/Test bağımsızlığı değişmez. Canonical bootstrap: `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`. Bu transition 6B'yi yürütmez.''', '### D-055 — Local manager takeover')
s = r('docs/START_HERE.md')
if '0. `AGENTS.md`' not in s:
    marker = '1. `docs/START_HERE.md`\n'
    if marker not in s: raise SystemExit('START_HERE reading order marker missing')
    w('docs/START_HERE.md', s.replace(marker, '0. `AGENTS.md`\n0a. `docs/LOCAL_MANAGER_HANDOFF.md`\n' + marker, 1))

# HANDOFF_STATE.
s = r('docs/HANDOFF_STATE.md')
if '- **D-055:**' not in s:
    marker = '- **D-054:** GNS-v0 Granularity & Naming Standard; 6A semantic decomposition/ID contract tamamlandı.\n'
    if marker not in s: raise SystemExit('HANDOFF D-054 marker missing')
    w('docs/HANDOFF_STATE.md', s.replace(marker, marker + "- **D-055:** Ana manager/koordinatör rolü local çalışan agent'a devredilebilir; takeover bootstrap `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`; project contracts/state değişmez.\n", 1))
before('docs/HANDOFF_STATE.md', '## 10. Tamamlanan aşamalar', '''## 9.1 D-055 / Local manager transition

Kullanıcı ana yönetici rolünü local çalışan agent'a devretme kararı verdi.
- Local manager D-024/D-027/D-050 protokolüne aynen uyar.
- İlk takeover: `AGENTS.md` → `LOCAL_MANAGER_HANDOFF` → `START_HERE` → `PROJECT_MEMORY_PROTOCOL` → bütün Markdown repo audit/read.
- Research/Coding/Test separation korunur.
- Transition numbered step değildir.
- Canonical execution değişmedi: 6A tamamlandı; 6B active/not-executed.
''', '## 9.1 D-055 / Local manager transition')

# PROJECT_CONTEXT — current-state section comes after section 10.
before('PROJECT_CONTEXT.md', '## 11. Güncel yürütme konumu', '''## 10.1 Local manager takeover — D-055

Ana manager/koordinatör rolü local çalışan agent'a devredilebilir. Canonical bootstrap `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`; GitHub durable source of truth ve D-024/D-027/D-050 PRE/POST workflow değişmez. Transition 6B'yi yürütmez; current execution hâlâ 6A ✅ / 6B 🟡 not-executed.
''', '## 10.1 Local manager takeover — D-055')

# MASTER_PLAN.
s = r('docs/MASTER_PLAN.md')
if '- D-055:' not in s:
    marker = '- D-052: 5C final V1 foundation backbone `FBB-v0`; canonical file `docs/V1_FOUNDATION_BACKBONE.md`.\n'
    if marker not in s: raise SystemExit('MASTER_PLAN D-052 marker missing')
    w('docs/MASTER_PLAN.md', s.replace(marker, marker + "- D-055: main manager role local çalışan agent'a devredilebilir; canonical bootstrap `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`; numbered execution state değişmez.\n", 1))
before('docs/MASTER_PLAN.md', '# Güncel Konum', '''# Local Manager Transition — D-055

Bu operasyonel handoff numaralı stage değildir. Local manager mevcut accepted specs'i devralır; PRE/POST GitHub memory protocol, Research/Coding/Test separation ve acceptance discipline aynen sürer. Bootstrap `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`. Transition **6B execution değildir**.

---
''', '# Local Manager Transition — D-055')

# EXECUTION_INDEX decision pointer.
s = r('docs/EXECUTION_INDEX.md')
if '- D-055:' not in s:
    marker = '- D-054: 6A final granularity/naming contract `GNS-v0`; semantic entity boundaries + stable logical ID rules.\n'
    if marker not in s: raise SystemExit('EXECUTION_INDEX D-054 marker missing')
    w('docs/EXECUTION_INDEX.md', s.replace(marker, marker + '- D-055: local çalışan agent main manager rolünü devralabilir; workflow contracts değişmez; transition 6B execution değildir.\n', 1))

# STEP_STATUS operational note; table/current state unchanged.
before('docs/STEP_STATUS.md', '## Repository memory hygiene — D-050', '''## Manager transition — D-055

Ana manager/koordinatör rolü local çalışan agent'a devredilebilir. Transition numbered step değildir ve current state'i değiştirmez: **6A ✅ / 6B 🟡 active-not-executed**. Bootstrap: `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md`.
''', '## Manager transition — D-055')

# README navigation.
s = r('README.md')
if '- `AGENTS.md` — local manager/agent bootstrap' not in s:
    marker = '- `docs/START_HERE.md` — yeni sohbet için başlangıç ve okuma sırası\n'
    if marker not in s: raise SystemExit('README START_HERE marker missing')
    rep = '- `AGENTS.md` — local manager/agent bootstrap ve non-negotiable workflow\n- `docs/LOCAL_MANAGER_HANDOFF.md` — local ana yönetici için kapsamlı takeover paketi\n' + marker
    w('README.md', s.replace(marker, rep, 1))

# Stable context + memory protocol.
append('docs/PROJECT_MASTER_CONTEXT.md', '''## D-055 / Local manager continuity guard

Ana manager/koordinatör rolü local çalışan agent'a taşınabilir; manager implementation değişikliği accepted product/learning/curriculum contracts'i değiştirmez. Local manager `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md` ile bootstrap olur, repo-wide Markdown takeover okuması yapar ve D-024/D-027/D-050 PRE/POST memory discipline'ine aynen uyar. GitHub durable source of truth olmaya devam eder; Research/Coding/Test bağımsızlığı korunur.
''', '## D-055 / Local manager continuity guard')
after('docs/PROJECT_MEMORY_PROTOCOL.md', "> **GitHub durable source of truth'tur. Sohbet hafızası veya tek bir durum dosyası repo içindeki başka bir stale dosyanın varlığını mazur göstermez.**\n", '''**D-055 clarification:** Ana yönetici local çalışan agent olsa da bu protokol aynen bağlayıcıdır. Local manager takeover bootstrap'ı `AGENTS.md` + `docs/LOCAL_MANAGER_HANDOFF.md` ile yapılır; manager implementation değişikliği PRE/POST kurallarını gevşetmez.''', 'D-055 clarification')

# Chronological progress.
append('docs/PROGRESS_LOG.md', '''### 2026-08-26 — Local manager takeover paketi hazırlandı — D-055

- Kullanıcı ana yönetici/koordinatör rolünü local çalışan agent'a devretme kararı verdi.
- Root `AGENTS.md` ve `docs/LOCAL_MANAGER_HANDOFF.md` takeover bootstrap'ı oluşturuldu.
- D-055 kalıcı workflow kararı kaydedildi; local manager olsa da GitHub durable source, D-024/D-027/D-050 ve Research/Coding/Test separation değişmez.
- `START_HERE`, `HANDOFF_STATE`, `PROJECT_CONTEXT`, `MASTER_PLAN`, `EXECUTION_INDEX`, `STEP_STATUS`, `AI_AGENT_WORKFLOW`, `PROJECT_MASTER_CONTEXT`, `PROJECT_MEMORY_PROTOCOL` ve README local-manager pointer/guard'larıyla hizalandı.
- Bu transition **6B'yi yürütmedi**. Canonical state: **6A ✅ GNS-v0 / D-054; 6B 🟡 active / not executed**.
- Local manager ilk numbered work öncesi tüm Markdown repo içeriğini okuyacak ve 6B için ayrıca fresh PRE-STEP refresh yapacak.
''', '### 2026-08-26 — Local manager takeover paketi hazırlandı — D-055')

print('Local manager handoff sync v2 applied.')
