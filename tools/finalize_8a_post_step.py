from __future__ import annotations

from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]


def read(rel: str) -> str:
    return (ROOT / rel).read_text(encoding="utf-8")


def write(rel: str, text: str) -> None:
    (ROOT / rel).write_text(text, encoding="utf-8")


def replace_required(text: str, old: str, new: str, label: str) -> str:
    if old in text:
        return text.replace(old, new, 1)
    if new in text:
        return text
    raise RuntimeError(f"{label}: required anchor missing")


# 1) Accept canonical 8A artifacts.
spec = read("docs/INFORMATION_ARCHITECTURE_SPEC.md")
spec = replace_required(
    spec,
    "**Status:** CANDIDATE — independent 8A QA pending  \n**Candidate decision:** `D-068`",
    "**Status:** ACCEPTED — independent 8A QA PASS  \n**Decision:** `D-068`",
    "8A spec acceptance",
)
spec = spec.replace(
    "Until independent QA and D-050 POST are complete, this document remains candidate rather than accepted.",
    "Independent 8A QA passed; D-050 POST living-memory and stale-reference audit are the final closure gates for the accepted step state.",
)
write("docs/INFORMATION_ARCHITECTURE_SPEC.md", spec)

ia = read("ux/8a_information_architecture/ia.yaml")
ia = replace_required(ia, "status: candidate_8a", "status: accepted_8a", "IA status")
ia = replace_required(ia, "candidate_decision: D-068", "decision: D-068", "IA decision")
write("ux/8a_information_architecture/ia.yaml", ia)

# 2) Execution index.
idx = read("docs/EXECUTION_INDEX.md")
idx = replace_required(
    idx,
    "- [ ] **8A — Bilgi mimarisi** **AKTİF**\n- [ ] **8B — Ana ekran**",
    "- [x] **8A — Bilgi mimarisi** — `UXIA-v0 / D-068`\n- [ ] **8B — Ana ekran** **AKTİF**",
    "EXECUTION_INDEX Stage 8",
)
write("docs/EXECUTION_INDEX.md", idx)

# 3) Step status table and current summary.
status = read("docs/STEP_STATUS.md")
status = replace_required(
    status,
    "| **8A — Bilgi mimarisi** | 🟡 Aktif | UX information architecture; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |\n| **8B–20** | ⬜ Bekliyor | 8A sonrası canonical sırada. |",
    "| **8A — Bilgi mimarisi** | ✅ | UXIA-v0 / D-068. Today/Learn/Progress/Profile semantic shell + shared detail/focused-flow IA; 49/49 QA PASS. |\n| **8B — Ana ekran** | 🟡 Aktif | Today content hierarchy / home UX; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |\n| **8C–20** | ⬜ Bekliyor | 8B sonrası canonical sırada. |",
    "STEP_STATUS table",
)
status = re.sub(
    r"## Son tamamlanan numaralı adım — 7E\n.*\Z",
    """## Son tamamlanan numaralı adım — 8A

**Final:** `UXIA-v0 — Adaptive Learning Information Architecture` / D-068.  
**Ana çıktı:** `docs/INFORMATION_ARCHITECTURE_SPEC.md` + `ux/8a_information_architecture/`.

8A sonucu:
- exactly 4 semantic top-level destination: `Today → Learn → Progress → Profile`,
- `Today` normal start destination,
- Assessment / Technical English / AI Tutor / remediation-retention engine'leri top-level silo değildir,
- shared `topic_detail`, `skill_detail`, `planner_explanation`, `assessment_report`, `technical_english_profile`, `learning_history`,
- focused `task_runner_flow` ve `assessment_session_flow`,
- browse hierarchy != prerequisite truth; UI/mastery truth separation preserved,
- planner explanation PDT-v0 trace-derived,
- progress time/streak/task-completion/general-CEFR overclaim yapmaz,
- adaptive layout semantic destination set/order'i değiştirmez,
- 49/49 independent validator check PASS.

## Aktif adım — 8B Ana ekran

**8B henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
""",
    status,
    flags=re.S,
)
write("docs/STEP_STATUS.md", status)

# 4) Project context.
ctx = read("PROJECT_CONTEXT.md")
ctx = replace_required(
    ctx,
    "- **8A 🟡 Bilgi mimarisi — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n- 8B–20 ⬜",
    "- **8A ✅ Bilgi mimarisi — UXIA-v0 / D-068**\n- **8B 🟡 Ana ekran — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n- 8C–20 ⬜",
    "PROJECT_CONTEXT current bullets",
)
ctx = ctx.replace(
    "**Sıradaki numaralı çalışma 8A'dır.** Fresh PRE-STEP + kullanıcı açık onayı olmadan yürütülmez.",
    "**Sıradaki numaralı çalışma 8B'dir.** Fresh PRE-STEP + kullanıcı açık onayı olmadan yürütülmez.",
)
if "## 11.1 8A UX Information Architecture — UXIA-v0 / D-068" not in ctx:
    anchor = "## 12. Proje hafızası / repository hygiene — D-050"
    if anchor not in ctx:
        raise RuntimeError("PROJECT_CONTEXT section 12 anchor missing")
    summary = """## 11.1 8A UX Information Architecture — UXIA-v0 / D-068

AŞAMA 8'in semantic information architecture temeli kilitlendi. Primary shell exactly four destination kullanır: `Today · Learn · Progress · Profile`; normal entry `Today`'dir. Assessment, Technical English, AI Tutor, retention/remediation gibi engine/workflow'lar ayrı top-level silo değildir. Exact Skill detail shared surface'tir; planner explanation PDT-v0 reason trace'ten türetilir; browse hierarchy prerequisite graph yerine geçmez. Visual layout/design system ve runtime navigation implementation 8B–10'a bırakılmıştır.

Canonical: `docs/INFORMATION_ARCHITECTURE_SPEC.md` / D-068.

"""
    ctx = ctx.replace(anchor, summary + anchor, 1)
write("PROJECT_CONTEXT.md", ctx)

# 5) Master plan Stage 8 and current state.
master = read("docs/MASTER_PLAN.md")
master = replace_required(
    master,
    "### [ ] 8A — Bilgi mimarisi — **AKTİF**\n### [ ] 8B — Ana ekran",
    "### [x] 8A — Bilgi mimarisi — UXIA-v0 / D-068\n**Final:** `docs/INFORMATION_ARCHITECTURE_SPEC.md` + `ux/8a_information_architecture/`\n\n- 4 top-level semantic destinations: Today / Learn / Progress / Profile,\n- Today normal start destination,\n- shared entity/detail surfaces + focused task/assessment flows,\n- Assessment/English/AI/remediation are contextual, not top-level silos,\n- exact Skill state + PDT explanation + TEPM profile truth ownership preserved,\n- no time/streak/task-completion/general-CEFR progress overclaim,\n- adaptive navigation semantics stable; concrete component/visual layout deferred,\n- independent 8A QA 49/49 PASS.\n\n### [ ] 8B — Ana ekran — **AKTİF**",
    "MASTER_PLAN Stage 8",
)
master = re.sub(
    r"# Güncel Konum\n.*\Z",
    """# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A–7E`, `8A`  
**Son tamamlanan:** **`8A — UXIA-v0 / D-068`**  
**Aktif:** **`8B — Ana ekran`** — henüz yürütülmedi.

Bir sonraki yürütme: **8B fresh PRE-STEP → UXIA-v0 Today ownership + accepted planner/capacity/explainability contracts üzerinden home/Today content hierarchy → independent QA → D-050 POST sync + stale audit.**

- D-068: 8A final `UXIA-v0`.
""",
    master,
    flags=re.S,
)
write("docs/MASTER_PLAN.md", master)

# 6) Handoff state: current section + durable 8A summary.
handoff = read("docs/HANDOFF_STATE.md")
handoff = replace_required(
    handoff,
    "- 8A 🟡 active-not-executed\n- 8B–20 ⬜",
    "- 8A ✅ UXIA-v0 / D-068\n- 8B 🟡 active-not-executed\n- 8C–20 ⬜",
    "HANDOFF stage list",
)
handoff = re.sub(
    r"## 11\. Güncel kesin konum\n\n\*\*Son tamamlanan:\*\* `7E — TEPM-v0 / D-067`  \n\*\*AŞAMA 7:\*\* ✅ TAMAMLANDI  \n\*\*Aktif:\*\* `8A — Bilgi mimarisi`  \n\*\*8A henüz yürütülmedi\. Fresh PRE-STEP \+ kullanıcı açık onayı zorunludur\.\*\*",
    "## 11. Güncel kesin konum\n\n**Son tamamlanan:** `8A — UXIA-v0 / D-068`  \n**Aktif:** `8B — Ana ekran`  \n**8B henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
    handoff,
    count=1,
)
if "## 20. D-068 / 8A final özeti" not in handoff:
    handoff += """

## 20. D-068 / 8A final özeti

Canonical: `docs/INFORMATION_ARCHITECTURE_SPEC.md`.  
IA/QA: `ux/8a_information_architecture/`.  
Research: `research/8a_information_architecture_research.md`.

UXIA-v0:
- exactly 4 semantic primary destination: Today / Learn / Progress / Profile,
- normal start = Today,
- 6 shared detail surface + 2 focused flow,
- Assessment/English/AI/remediation-retention are contextual, not shell silos,
- exact Skill detail is shared; browse hierarchy never substitutes PRG-v0 prerequisite truth,
- PDT-v0 owns planner explanations; assessment report does not own mastery,
- TEPM-v0 owns qualified Technical English profile semantics,
- local/offline/AI-degraded/recovery states remain cross-cutting,
- concrete visual layout/component implementation is deferred to 8B–10,
- independent 8A QA 49/49 PASS.

## 21. 8B handoff

8B — Ana ekran; UXIA-v0 `today` information ownership üzerinden Today/home content hierarchy, next-action emphasis, plan summary, capacity context and attention/reason presentation'ını tasarlayacaktır. 8B fresh PRE + kullanıcı açık onayı olmadan yürütülmez.
"""
write("docs/HANDOFF_STATE.md", handoff)

# 7) Start Here: add IA reading order/current state.
start = read("docs/START_HERE.md")
if "11ah. `docs/INFORMATION_ARCHITECTURE_SPEC.md`" not in start:
    anchor = "11ag. `curriculum/english/7e_mastery_profile/qa_report.yaml`\n"
    if anchor not in start:
        raise RuntimeError("START_HERE read-order anchor missing")
    start = start.replace(anchor, anchor + "11ah. `docs/INFORMATION_ARCHITECTURE_SPEC.md`\n11ai. `ux/8a_information_architecture/ia.yaml`\n11aj. `ux/8a_information_architecture/qa_report.yaml`\n", 1)
start = replace_required(
    start,
    "- AŞAMA 7 ✅ **EED-v0 / D-063 → TECP-v0 / D-064 → DECP-v0 / D-065 → TEIP-v0 / D-066 → TEPM-v0 / D-067**",
    "- AŞAMA 7 ✅ **EED-v0 / D-063 → TECP-v0 / D-064 → DECP-v0 / D-065 → TEIP-v0 / D-066 → TEPM-v0 / D-067**\n- AŞAMA 8: **8A ✅ UXIA-v0 / D-068; 8B 🟡 active-not-executed**",
    "START_HERE stage summary",
)
start = re.sub(
    r"## 9\. Güncel çalışma konumu\n.*?(?=## 10\.)",
    """## 9. Güncel çalışma konumu

**Son tamamlanan:** **`8A — UXIA-v0 / D-068`**  
**Aktif:** **`8B — Ana ekran`**  
**8B henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**

""",
    start,
    flags=re.S,
)
start = re.sub(
    r"> `xpike-dgm/ai-infra-learning-coach reposunda AGENTS\.md.*?`$",
    "> `xpike-dgm/ai-infra-learning-coach reposunda AGENTS.md + SESSION_START + START_HERE + PROJECT_MEMORY_PROTOCOL ile başla. Current execution için EXECUTION_INDEX + STEP_STATUS + HANDOFF_STATE + PROJECT_CONTEXT + MASTER_PLAN'ı fresh çapraz doğrula. 8A UXIA-v0 / D-068 tamamlandı: semantic shell Today · Learn · Progress · Profile; Assessment/English/AI/remediation contextual; shared Skill detail + PDT-v0 explanation; visual implementation henüz kilitli değil. Aktif step 8B — Ana ekran; 8B henüz yürütülmedi. Numaralı adımı fresh PRE ve kullanıcı açık onayı olmadan yürütme.`",
    start,
    flags=re.M,
)
write("docs/START_HERE.md", start)

# 8) AGENTS current execution state.
agents = read("AGENTS.md")
agents = replace_required(
    agents,
    "- **Aktif adım: 8A — Bilgi mimarisi.**\n- **8A henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.\n- AŞAMA 8B–20 bekliyor.\n\n**8A'yı bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 8A için ayrıca fresh PRE-STEP refresh yap ve kullanıcı açık onayını doğrula.",
    "- 8A: ✅ `UXIA-v0 / D-068` tamamlandı — Today/Learn/Progress/Profile semantic information architecture; shared detail/focused-flow ownership; 49/49 QA PASS.\n- **Aktif adım: 8B — Ana ekran.**\n- **8B henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.\n- AŞAMA 8C–20 bekliyor.\n\n**8B'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 8B için ayrıca fresh PRE-STEP refresh yap ve kullanıcı açık onayını doğrula.",
    "AGENTS current state",
)
write("AGENTS.md", agents)

# 9) Decisions append-only.
decisions = read("docs/DECISIONS.md")
if "## D-068 — UX Information Architecture = UXIA-v0" not in decisions:
    decisions += """

## D-068 — UX Information Architecture = UXIA-v0
**Durum:** Kabul edildi — 2026-08-27

- 8A final modeli `UXIA-v0 — Adaptive Learning Information Architecture` oldu.
- Primary semantic shell exactly four destination kullanır ve sıra sabittir: `Today → Learn → Progress → Profile`; normal start `Today`'dir.
- Assessment, Technical English, AI Tutor/chat, mastery/retention/remediation/weakness ayrı top-level destination değildir; kendi workflow/context/home'larında görünür.
- Shared semantic detail surfaces: Topic, Skill, planner explanation, assessment report, Technical English profile, learning history. Aynı Skill için Learn/Progress altında farklı truth kopyaları üretilmez.
- Task Runner ve assessment session focused flow'dur; shell'den ayrılabilir fakat safe pause/exit ve deterministic semantic return gerekir.
- UI hierarchy prerequisite truth değildir; planner reason PDT-v0 trace'ten, English profile TEPM-v0'dan, mastery GRE/RVR pipeline'ından türetilir.
- Progress career %, elapsed time, streak, task completion veya numeric/general CEFR'i primary mastery truth olarak kullanamaz.
- Compact/expanded navigation component'i 8A'da kilitlenmedi; semantic destination identity/order window size ile değişmez.
- Final visual hierarchy 8B–8G'ye, implementation 9–10'a bırakıldı.
- Independent validator: 49/49 PASS; 4 primary destination / 6 shared detail / 2 focused flow / 17 information object / 7 research source.

Ayrıntı: `docs/INFORMATION_ARCHITECTURE_SPEC.md`.
"""
write("docs/DECISIONS.md", decisions)

# 10) Progress log append-only.
progress = read("docs/PROGRESS_LOG.md")
if "## 2026-08-27 — 8A Bilgi mimarisi tamamlandı — UXIA-v0 / D-068" not in progress:
    progress += """

## 2026-08-27 — 8A Bilgi mimarisi tamamlandı — UXIA-v0 / D-068

- Kullanıcı açık onayı sonrası fresh PRE yapıldı; PRE sırasında `HANDOFF_STATE` ve `LOCAL_MANAGER_HANDOFF` içinde 7E sonrası kalan stale current-state mirror'ları bulundu ve execution başlamadan deterministic repair + external-memory QA ile temizlendi.
- Android adaptive/navigation guidance, W3C consistent navigation/identification ve NN/g mobile IA/progressive-disclosure research girdisi incelendi; fixed visual/card-count gibi unsupported precision türetilmedi.
- Exactly four semantic top-level destination kabul edildi: Today / Learn / Progress / Profile; Today normal start.
- Assessment, English, AI Tutor, remediation/retention top-level silo yapılmadı; contextual workflow/state olarak doğru information owner'a bağlandı.
- Shared entity/detail surfaces ve focused Task Runner / assessment flows tanımlandı; browse hierarchy != prerequisite truth ve UI != mastery truth invariant'ları korundu.
- Candidate core QA external-memory + Stage 6 + accepted Stage 7 regressions ile birlikte PASS; independent 8A validator 49/49 PASS.
- D-050 POST accepted artifact revalidation + living-memory sync + repo-wide stale-reference audit ile 8A kapatıldı; 8B active-not-executed yapıldı.

**Sonraki kesin adım:** `8B — Ana ekran`. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
"""
write("docs/PROGRESS_LOG.md", progress)

# 11) Local manager handoff current markers + addendum.
local = read("docs/LOCAL_MANAGER_HANDOFF.md")
local = local.replace("8A 🟡 active-not-executed", "8A ✅ UXIA-v0 / D-068\n- 8B 🟡 active-not-executed")
local = local.replace("8A henüz yürütülmedi", "8B henüz yürütülmedi")
local = re.sub(r"(Sıradaki gerçek numbered work:\*\*\s*`)8A\b[^`]*`", r"\g<1>8B — Ana ekran`", local)
if "# 8A completion addendum — D-068" not in local:
    local += """

---

# 8A completion addendum — D-068

8A `UXIA-v0 — Adaptive Learning Information Architecture` ile tamamlandı.

- semantic primary shell: Today / Learn / Progress / Profile,
- Assessment/Technical English/AI/remediation-retention contextual; ayrı top-level silo değil,
- shared Topic/Skill/explanation/report/English-profile/history detail family,
- focused Task Runner + assessment-session flows,
- UI hierarchy prerequisite truth veya mastery source of truth değildir,
- PDT-v0 explanation, TEPM-v0 profile ve canonical engine ownership korunur,
- independent QA 49/49 PASS.

**Sıradaki gerçek numbered work:** `8B — Ana ekran`.  
**8B henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**
"""
write("docs/LOCAL_MANAGER_HANDOFF.md", local)

# 12) Vault current context / open loops.
vctx = read("vault/agent/CURRENT_CONTEXT.md")
vctx = re.sub(
    r"## Exact execution state\n\n.*?(?=\n## Immediate open loops)",
    """## Exact execution state

AŞAMA 6 ve AŞAMA 7 tamamlandı. 8A da **UXIA-v0 / D-068** ile tamamlandı: primary semantic shell `Today · Learn · Progress · Profile`; Assessment/English/AI/remediation contextual; shared Skill detail + PDT-v0 explanation ownership preserved. Son tamamlanan adım **8A — Bilgi mimarisi**. Aktif adım **8B — Ana ekran**; henüz yürütülmedi. 8B başlamadan fresh PRE-STEP + kullanıcı açık onayı zorunludur.

Bu snapshot'tan daha güncel veya çelişen bir iddia varsa [[vault/wiki/sources/Execution State Source|living-memory seti]] kazanır.
""",
    vctx,
    flags=re.S,
)
write("vault/agent/CURRENT_CONTEXT.md", vctx)

loops = read("vault/agent/OPEN_LOOPS.md")
loops = replace_required(
    loops,
    "- [ ] 8A Bilgi mimarisi **AKTİF**: accepted product/learning/planner/assessment/curriculum/English contracts üzerinden UX information architecture.",
    "- [x] 8A Bilgi mimarisi: UXIA-v0 / D-068 ile tamamlandı; Today/Learn/Progress/Profile semantic IA + shared detail/focused flows.\n- [ ] 8B Ana ekran **AKTİF**: UXIA-v0 `today` ownership üzerinden Today/home content hierarchy ve next-action UX.",
    "OPEN_LOOPS",
)
write("vault/agent/OPEN_LOOPS.md", loops)

# 13) Session handoff note.
session = ROOT / "vault/agent/session-logs/2026-08-27-8a-information-architecture.md"
if not session.exists():
    session.write_text("""---
type: session-handoff
status: completed
stage_step: 8A
decision: D-068
model: UXIA-v0
---

# 2026-08-27 — 8A Information Architecture

- User explicitly approved 8A.
- Fresh PRE detected stale 7E current-state mirrors in living handoff sources; repaired before semantic execution.
- Research: Android navigation/adaptive patterns, W3C consistent navigation/identification, NN/g mobile IA/progressive disclosure.
- Accepted semantic shell: Today / Learn / Progress / Profile.
- Assessment, English, AI Tutor, remediation/retention remain contextual rather than top-level silos.
- Shared detail and focused-flow IA preserve canonical mastery/prerequisite/planner/English truth ownership.
- Independent 8A QA: 49/49 PASS.
- POST state: 8A complete; 8B active-not-executed; fresh PRE + explicit approval required.
""", encoding="utf-8")

# Final assertions for deterministic POST state.
required = {
    "docs/EXECUTION_INDEX.md": ["[x] **8A — Bilgi mimarisi** — `UXIA-v0 / D-068`", "**8B — Ana ekran** **AKTİF**"],
    "docs/STEP_STATUS.md": ["**8A — Bilgi mimarisi** | ✅", "**8B — Ana ekran** | 🟡 Aktif"],
    "docs/HANDOFF_STATE.md": ["**Son tamamlanan:** `8A — UXIA-v0 / D-068`", "**Aktif:** `8B — Ana ekran`"],
    "PROJECT_CONTEXT.md": ["8A ✅ Bilgi mimarisi — UXIA-v0 / D-068", "8B 🟡 Ana ekran — AKTİF"],
    "docs/MASTER_PLAN.md": ["[x] 8A — Bilgi mimarisi — UXIA-v0 / D-068", "[ ] 8B — Ana ekran — **AKTİF**"],
    "docs/START_HERE.md": ["8A ✅ UXIA-v0 / D-068", "**Aktif:** **`8B — Ana ekran`**"],
    "AGENTS.md": ["8A: ✅ `UXIA-v0 / D-068`", "**Aktif adım: 8B — Ana ekran.**"],
    "vault/agent/CURRENT_CONTEXT.md": ["UXIA-v0 / D-068", "Aktif adım **8B — Ana ekran**"],
    "vault/agent/OPEN_LOOPS.md": ["[x] 8A Bilgi mimarisi", "[ ] 8B Ana ekran **AKTİF**"],
}
for rel, markers in required.items():
    data = read(rel)
    missing = [m for m in markers if m not in data]
    if missing:
        raise RuntimeError(f"{rel}: missing POST markers {missing}")

print("8A_POST_FINALIZER=PASS")
