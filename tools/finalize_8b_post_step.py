from __future__ import annotations

from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]


def read(rel: str) -> str:
    return (ROOT / rel).read_text(encoding="utf-8")


def write(rel: str, text: str) -> None:
    path = ROOT / rel
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text, encoding="utf-8")


def replace_required(text: str, old: str, new: str, label: str) -> str:
    if old in text:
        return text.replace(old, new, 1)
    if new in text:
        return text
    raise RuntimeError(f"{label}: required anchor missing")


# 1) Accept canonical 8B artifacts.
spec = read("docs/TODAY_HOME_SCREEN_SPEC.md")
spec = replace_required(
    spec,
    "**Status:** CANDIDATE — independent 8B QA required  \n**Candidate decision:** `D-069`",
    "**Status:** ACCEPTED — independent 8B QA PASS  \n**Decision:** `D-069`",
    "8B spec acceptance",
)
write("docs/TODAY_HOME_SCREEN_SPEC.md", spec)

home = read("ux/8b_today_home/home.yaml")
home = replace_required(home, "status: candidate_8b", "status: accepted_8b", "8B YAML status")
home = replace_required(home, "candidate_decision: D-069", "decision: D-069", "8B YAML decision")
write("ux/8b_today_home/home.yaml", home)

# 2) Permanent decision.
decisions = read("docs/DECISIONS.md")
if "## D-069 — Today Home UX = THUX-v0" not in decisions:
    decisions = decisions.rstrip() + """


## D-069 — Today Home UX = THUX-v0
**Durum:** Kabul edildi — 2026-08-28

- 8B final modeli `THUX-v0 — Today Home UX` oldu.
- Canonical spec `docs/TODAY_HOME_SCREEN_SPEC.md`; machine-readable contract `ux/8b_today_home/home.yaml`; research/contract synthesis `research/8b_today_home_research.md`.
- Today ana soruyu action-first biçimde cevaplar: `Bugün şimdi ne yapmalıyım?`; Today canonical planner/state truth'un projection'ıdır, ikinci planner/mastery engine değildir.
- Semantic content hierarchy `primary_action → day_plan_context → remaining_plan → conditional attention_context → supporting_navigation` olarak kilitlendi; bu sıra fixed card/pixel geometry değildir.
- Dominant action precedence data-recovery safety → valid revalidated resume → selected next PlannedTask → loading/replanning → valid empty/capacity-limited → recoverable error şeklindedir.
- Today kuyruğu yalnız current selected `PlannedTask`'ları temsil eder; bütün LearningNeed/TaskCandidate evreni, eski gün backlog'u veya debt listesi değildir. Blocked dependent work startable gösterilemez.
- Daily capacity hard time budget context'idir, progress/mastery değildir. Today current-day override sağlayabilir ve override replan tetikler; persistent capacity preference Profile-owned kalır.
- Task purpose/activity/track ayrımı korunur; duration estimate'tir ve mastery signal değildir; task completion mastery/failure çıkarımı yapamaz.
- Planner reason snippet'i yalnız PDT-v0 trace facts'ten türetilir: overview'da bir primary + en fazla bir materially useful supporting reason; full explanation shared `planner_explanation` surface'indedir.
- Assessment Today'de contextual PlannedTask/attention olarak görünür; daily quota/permanent gradebook yoktur ve assessment score broad mastery truth değildir.
- Technical English common capacity içinde contextual track'tir; separate budget/fixed quota/streak/debt/general-CEFR claim yoktur.
- SRR-v0 korunur: missed day backlog/debt/failure üretmez; Today current state'ten fresh plan gösterir.
- Empty/loading/replanning/offline/AI-degraded/recovery states semantically ayrılır; `no task today` all-mastered/professional-ready anlamına gelmez.
- 8B final visual design, fixed card count/pixels, task-runner choreography, assessment interaction, Skill/progress visualization ve implementation technology'yi kilitlemez; sahipleri 8C–10/17–18'dir.
- Independent 8B QA: **90/90 PASS**; 5 semantic content region / 12 semantic state / 11 forbidden Home anti-pattern. Stage 6, Stage 7, accepted 8A ve external-memory regressions PASS.
- Sonraki numbered step `8C — Günlük çalışma akışı`; fresh PRE + kullanıcı açık onayı olmadan yürütülmez.

Ayrıntı: `docs/TODAY_HOME_SCREEN_SPEC.md`.
"""
write("docs/DECISIONS.md", decisions)

# 3) Execution index.
idx = read("docs/EXECUTION_INDEX.md")
idx = replace_required(
    idx,
    "- [ ] **8B — Ana ekran** **AKTİF**\n- [ ] **8C — Günlük çalışma akışı**",
    "- [x] **8B — Ana ekran** — `THUX-v0 / D-069`\n- [ ] **8C — Günlük çalışma akışı** **AKTİF**",
    "EXECUTION_INDEX Stage 8",
)
idx = re.sub(
    r"# Güncel Konum\n\n\*\*Tamamlanan:\*\* `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A–7E`, `8A`  \n\*\*Son tamamlanan:\*\* \*\*`8A — UXIA-v0 / D-068`\*\*  \n\*\*Aktif:\*\* \*\*`8B — Ana ekran`\*\* — active-not-executed\n\n8A `UXIA-v0`.*?8B başlamadan fresh PRE-STEP GitHub refresh \+ kullanıcı açık onayı zorunludur\.",
    """# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A–7E`, `8A–8B`  
**Son tamamlanan:** **`8B — THUX-v0 / D-069`**  
**Aktif:** **`8C — Günlük çalışma akışı`** — active-not-executed

8B `THUX-v0` ile Today/Home action-first hierarchy, current PlannedTask queue, hard-capacity context, PDT-v0 reason projection ve truthful empty/degraded states kilitlendi; completion/mastery, backlog/debt, English quota ve gradebook shortcuts yasaktır.

8C başlamadan fresh PRE-STEP GitHub refresh + kullanıcı açık onayı zorunludur.""",
    idx,
    count=1,
    flags=re.S,
)
write("docs/EXECUTION_INDEX.md", idx)

# 4) Step status.
status = read("docs/STEP_STATUS.md")
status = replace_required(
    status,
    "| **8B — Ana ekran** | 🟡 Aktif | Today content hierarchy / home UX; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |\n| **8C–20** | ⬜ Bekliyor | 8B sonrası canonical sırada. |",
    "| **8B — Ana ekran** | ✅ | THUX-v0 / D-069. Action-first Today hierarchy + current PlannedTask queue + capacity/reason/empty/degraded semantics; 90/90 QA PASS. |\n| **8C — Günlük çalışma akışı** | 🟡 Aktif | Task Runner / daily-session interaction choreography; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |\n| **8D–20** | ⬜ Bekliyor | 8C sonrası canonical sırada. |",
    "STEP_STATUS table",
)
status = status.replace("## Güncel durum — 2026-08-27", "## Güncel durum — 2026-08-28")
status = re.sub(
    r"## Son tamamlanan numaralı adım — 8A\n.*\Z",
    """## Son tamamlanan numaralı adım — 8B

**Final:** `THUX-v0 — Today Home UX` / D-069.  
**Ana çıktı:** `docs/TODAY_HOME_SCREEN_SPEC.md` + `ux/8b_today_home/`.

8B sonucu:
- Today/Home action-first semantic hierarchy: `primary_action → day_plan_context → remaining_plan → conditional attention → supporting navigation`,
- current queue only selected `PlannedTask`; candidate/backlog/debt leakage yok,
- data recovery / revalidated resume / next task / replanning / empty / recoverable-error precedence,
- daily capacity hard time budget; Today override → replan; persistent preference Profile-owned,
- purpose/activity/track separated; duration estimate; completion != mastery,
- PDT-v0 trace-backed bounded reason summary + shared full explanation,
- contextual assessment; no daily quota/gradebook/broad-score mastery,
- contextual Technical English; no separate budget/quota/streak/debt/general CEFR,
- SRR-v0 missed-day fresh-plan behavior preserved,
- 12 truthful loading/ready/empty/offline/AI-degraded/recovery semantic state,
- final visual geometry/design system and 8C/8D interaction choreography deferred,
- independent validator **90/90 PASS**; Stage 6 + Stage 7 + 8A + external-memory regressions PASS.

## Aktif adım — 8C Günlük çalışma akışı

**8C henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
""",
    status,
    flags=re.S,
)
write("docs/STEP_STATUS.md", status)

# 5) Project context.
ctx = read("PROJECT_CONTEXT.md")
ctx = ctx.replace("**Son senkron:** 2026-08-27", "**Son senkron:** 2026-08-28")
ctx = replace_required(
    ctx,
    "- **8B 🟡 Ana ekran — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n- 8C–20 ⬜",
    "- **8B ✅ Ana ekran — THUX-v0 / D-069**\n- **8C 🟡 Günlük çalışma akışı — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n- 8D–20 ⬜",
    "PROJECT_CONTEXT current bullets",
)
ctx = ctx.replace(
    "**Sıradaki numaralı çalışma 8B'dir.** Fresh PRE-STEP + kullanıcı açık onayı olmadan yürütülmez.",
    "**Sıradaki numaralı çalışma 8C'dir.** Fresh PRE-STEP + kullanıcı açık onayı olmadan yürütülmez.",
)
if "## 11.2 8B Today Home UX — THUX-v0 / D-069" not in ctx:
    anchor = "## 12. Proje hafızası / repository hygiene — D-050"
    if anchor not in ctx:
        raise RuntimeError("PROJECT_CONTEXT section 12 anchor missing")
    summary = """## 11.2 8B Today Home UX — THUX-v0 / D-069

Today/Home semantic contract action-first olarak kilitlendi. Dominant content current valid action'dır; queue yalnız selected PlannedTask'lardan oluşur. Daily capacity time budget context'idir, mastery/progress değildir; Today current-day override ile replan tetikleyebilir. Planner reasons PDT-v0 trace facts'ten bounded biçimde türetilir. Assessment ve Technical English contextual kalır; quota/streak/debt/gradebook shortcuts yoktur. Missed-day fresh-plan, offline/local-core, AI-degraded ve data-recovery semantics açıkça ayrılır. Fixed visual/card/pixel geometry 8F/8G'ye; Task Runner interaction choreography 8C'ye bırakılmıştır.

Canonical: `docs/TODAY_HOME_SCREEN_SPEC.md` / D-069.

"""
    ctx = ctx.replace(anchor, summary + anchor, 1)
write("PROJECT_CONTEXT.md", ctx)

# 6) Master plan.
master = read("docs/MASTER_PLAN.md")
master = replace_required(
    master,
    "### [ ] 8B — Ana ekran — **AKTİF**\n### [ ] 8C — Günlük çalışma akışı",
    """### [x] 8B — Ana ekran — THUX-v0 / D-069
**Final:** `docs/TODAY_HOME_SCREEN_SPEC.md` + `ux/8b_today_home/`

- action-first primary action + plan/capacity context,
- remaining queue only current selected PlannedTasks; no candidate/backlog/debt leakage,
- safe recovery/resume/next-task/replan/empty precedence,
- current-day capacity override triggers replan; persistent settings remain Profile-owned,
- PDT-v0 bounded reason projection; attention never rescores planner priority,
- contextual assessment + Technical English without quotas/gradebook/general CEFR,
- truthful empty/offline/AI-degraded/recovery states,
- task completion != mastery; missed day != debt,
- 90/90 independent 8B QA PASS.

### [ ] 8C — Günlük çalışma akışı — **AKTİF**""",
    "MASTER_PLAN Stage 8",
)
master = re.sub(
    r"# Güncel Konum\n.*\Z",
    """# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A–7E`, `8A–8B`  
**Son tamamlanan:** **`8B — THUX-v0 / D-069`**  
**Aktif:** **`8C — Günlük çalışma akışı`** — henüz yürütülmedi.

Bir sonraki yürütme: **8C fresh PRE-STEP → THUX-v0 Home entry/return + UXIA-v0 focused Task Runner ownership + accepted teaching/evidence/planner contracts üzerinden daily task/session choreography → independent QA → D-050 POST sync + stale audit.**

- D-068: 8A final `UXIA-v0`.
- D-069: 8B final `THUX-v0`.
""",
    master,
    flags=re.S,
)
write("docs/MASTER_PLAN.md", master)

# 7) Handoff state.
handoff = read("docs/HANDOFF_STATE.md")
handoff = handoff.replace("**Son güncelleme:** 2026-08-27", "**Son güncelleme:** 2026-08-28")
handoff = replace_required(
    handoff,
    "- 8A ✅ UXIA-v0 / D-068\n- 8B 🟡 active-not-executed\n- 8C–20 ⬜",
    "- 8A ✅ UXIA-v0 / D-068\n- 8B ✅ THUX-v0 / D-069\n- 8C 🟡 active-not-executed\n- 8D–20 ⬜",
    "HANDOFF stage list",
)
handoff = replace_required(
    handoff,
    "**Son tamamlanan:** `8A — UXIA-v0 / D-068`  \n**Aktif:** `8B — Ana ekran`  \n**8B henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
    "**Son tamamlanan:** `8B — THUX-v0 / D-069`  \n**Aktif:** `8C — Günlük çalışma akışı`  \n**8C henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
    "HANDOFF exact state",
)
if "## 22. D-069 / 8B final özeti" not in handoff:
    handoff += """

## 22. D-069 / 8B final özeti

Canonical: `docs/TODAY_HOME_SCREEN_SPEC.md`.  
Contract/QA: `ux/8b_today_home/`.  
Synthesis: `research/8b_today_home_research.md`.

THUX-v0:
- Today is canonical planner/state projection, not a second planner,
- 5 semantic content regions with primary action dominant,
- current queue only selected PlannedTasks; no candidate/backlog/debt list,
- recovery → revalidated resume → next task → replan/loading → valid empty/capacity-limited → recoverable-error precedence,
- daily capacity is hard time budget; Today override may replan; persistent preference Profile-owned,
- PDT-v0 owns bounded user-facing reasons,
- assessment/Technical English contextual; quota/streak/gradebook/general-CEFR shortcuts forbidden,
- task completion != mastery; missed day != debt,
- 12 semantic overview states including offline local-core, AI-degraded and data-recovery,
- visual system/geometry and focused interaction choreography remain deferred,
- independent 8B QA 90/90 PASS; Stage 6/7/8A regressions + external memory PASS.

## 23. 8C handoff

8C — Günlük çalışma akışı. THUX-v0 primary action/resume/return semantics, UXIA-v0 `task_runner_flow` ownership, learning/evidence/assistance contracts ve deterministic replan behavior üzerinden focused daily Task Runner/session choreography tasarlanacaktır. 8C fresh PRE + kullanıcı açık onayı olmadan yürütülmez.
"""
write("docs/HANDOFF_STATE.md", handoff)

# 8) START_HERE.
start = read("docs/START_HERE.md")
if "### D-069 — THUX-v0" not in start:
    anchor = "## 4. Güncel stage mapping"
    if anchor not in start:
        raise RuntimeError("START_HERE stage mapping anchor missing")
    decision_summary = """### D-068 — UXIA-v0
8A final `docs/INFORMATION_ARCHITECTURE_SPEC.md`; Today/Learn/Progress/Profile semantic shell, shared detail surfaces ve focused flows kilitlendi.

### D-069 — THUX-v0
8B final `docs/TODAY_HOME_SCREEN_SPEC.md`; Today/Home action-first hierarchy, current PlannedTask queue, hard-capacity context, PDT-v0 reason projection ve truthful empty/degraded states kilitlendi. Fixed visual geometry ve Task Runner choreography daha sonraki adımlardadır.

"""
    start = start.replace(anchor, decision_summary + anchor, 1)
start = replace_required(
    start,
    "- 8 UX — **8A ✅ UXIA-v0 / D-068**\n  - **8B 🟡 active-not-executed**",
    "- 8 UX — **8A ✅ UXIA-v0 / D-068; 8B ✅ THUX-v0 / D-069**\n  - **8C 🟡 active-not-executed**",
    "START_HERE stage map",
)
if "11ak. `docs/TODAY_HOME_SCREEN_SPEC.md`" not in start:
    anchor = "11aj. `ux/8a_information_architecture/qa_report.yaml`\n"
    if anchor not in start:
        raise RuntimeError("START_HERE read order 8A anchor missing")
    start = start.replace(anchor, anchor + "11ak. `docs/TODAY_HOME_SCREEN_SPEC.md`\n11al. `ux/8b_today_home/home.yaml`\n11am. `ux/8b_today_home/qa_report.yaml`\n", 1)
start = replace_required(
    start,
    "- AŞAMA 8A ✅ **UXIA-v0 / D-068**\n- AŞAMA 8B 🟡 **active-not-executed**",
    "- AŞAMA 8A ✅ **UXIA-v0 / D-068**\n- AŞAMA 8B ✅ **THUX-v0 / D-069**\n- AŞAMA 8C 🟡 **active-not-executed**",
    "START_HERE completed core",
)
start = replace_required(
    start,
    "**Son tamamlanan:** **`8A — UXIA-v0 / D-068`**  \n**Aktif:** **`8B — Ana ekran`**  \n**8B henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
    "**Son tamamlanan:** **`8B — THUX-v0 / D-069`**  \n**Aktif:** **`8C — Günlük çalışma akışı`**  \n**8C henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
    "START_HERE current state",
)
start = re.sub(
    r"> `xpike-dgm/ai-infra-learning-coach reposunda AGENTS\.md \+ SESSION_START.*?`$",
    "> `xpike-dgm/ai-infra-learning-coach reposunda AGENTS.md + SESSION_START + START_HERE + PROJECT_MEMORY_PROTOCOL ile başla. Current execution için EXECUTION_INDEX + STEP_STATUS + HANDOFF_STATE + PROJECT_CONTEXT + MASTER_PLAN'ı fresh çapraz doğrula. 8A UXIA-v0 / D-068 ve 8B THUX-v0 / D-069 tamamlandı. Today action-first; queue current selected PlannedTasks; capacity time budget; reasons PDT-v0 trace-derived; completion != mastery; missed-day debt yok; assessment/English contextual. Aktif step 8C — Günlük çalışma akışı; 8C henüz yürütülmedi. Numaralı adımı fresh PRE ve kullanıcı açık onayı olmadan yürütme.`",
    start,
    flags=re.M,
)
write("docs/START_HERE.md", start)

# 9) AGENTS current state.
agents = read("AGENTS.md")
agents = replace_required(
    agents,
    "- 8A: ✅ `UXIA-v0 / D-068` tamamlandı — Today/Learn/Progress/Profile semantic information architecture; shared detail/focused-flow ownership; 49/49 QA PASS.\n- **Aktif adım: 8B — Ana ekran.**\n- **8B henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.\n- AŞAMA 8C–20 bekliyor.\n\n**8B'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 8B için ayrıca fresh PRE-STEP refresh yap ve kullanıcı açık onayını doğrula.",
    "- 8A: ✅ `UXIA-v0 / D-068` tamamlandı — Today/Learn/Progress/Profile semantic information architecture; shared detail/focused-flow ownership; 49/49 QA PASS.\n- 8B: ✅ `THUX-v0 / D-069` tamamlandı — action-first Today/Home hierarchy, current PlannedTask queue, hard-capacity/reason/empty/degraded semantics; 90/90 QA PASS.\n- **Aktif adım: 8C — Günlük çalışma akışı.**\n- **8C henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.\n- AŞAMA 8D–20 bekliyor.\n\n**8C'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 8C için ayrıca fresh PRE-STEP refresh yap ve kullanıcı açık onayını doğrula.",
    "AGENTS current state",
)
write("AGENTS.md", agents)

# 10) LOCAL_MANAGER_HANDOFF living mirrors.
lmh = read("docs/LOCAL_MANAGER_HANDOFF.md")
lmh = replace_required(
    lmh,
    "8. Current execution state'in `AŞAMA 6 ✅ / AŞAMA 7 ✅ / 8A ✅ UXIA-v0 / D-068`; aktif adımın `8B active-not-executed` olduğunu living-memory setiyle doğrula.",
    "8. Current execution state'in `AŞAMA 6 ✅ / AŞAMA 7 ✅ / 8A ✅ UXIA-v0 / D-068 / 8B ✅ THUX-v0 / D-069`; aktif adımın `8C active-not-executed` olduğunu living-memory setiyle doğrula.",
    "LOCAL_MANAGER bootstrap state",
)
lmh = lmh.replace(
    "- AŞAMA 8A ✅ UXIA-v0 / D-068\n- 8B 🟡 active-not-executed\n- 8B–20 ⬜",
    "- AŞAMA 8A ✅ UXIA-v0 / D-068\n- AŞAMA 8B ✅ THUX-v0 / D-069\n- AŞAMA 8C 🟡 active-not-executed\n- 8D–20 ⬜",
)
lmh = re.sub(
    r"# 22\. Current exact state — en kritik takeover bilgisi\n\n\*\*Son tamamlanan numaralı adım:\*\* `8A — Bilgi mimarisi`.*?```text\nfresh 8B PRE-STEP GitHub refresh\n→ user explicit approval verification\n→ 8B execution\n→ independent QA\n→ D-050 POST sync\n→ repo-wide stale-reference audit\n```",
    """# 22. Current exact state — en kritik takeover bilgisi

**Son tamamlanan numaralı adım:** `8B — Ana ekran`  
**Final:** `THUX-v0 — Today Home UX` / D-069  
**Canonical:** `docs/TODAY_HOME_SCREEN_SPEC.md` + `ux/8b_today_home/`

**AŞAMA 7:** ✅ TAMAMLANDI  
**AŞAMA 8A:** ✅ TAMAMLANDI  
**AŞAMA 8B:** ✅ TAMAMLANDI  
**Aktif adım:** `8C — Günlük çalışma akışı`  
**Durum:** **HENÜZ YÜRÜTÜLMEDİ**

8B Today'i canonical planner/state projection olarak kilitledi: dominant valid action, current selected PlannedTask queue, hard capacity context, PDT-v0 reason projection, contextual assessment/English ve truthful empty/degraded/recovery states. Completion mastery değildir; missed day debt değildir.

8C için:

```text
fresh 8C PRE-STEP GitHub refresh
→ user explicit approval verification
→ 8C execution
→ independent QA
→ D-050 POST sync
→ repo-wide stale-reference audit
```""",
    lmh,
    count=1,
    flags=re.S,
)
write("docs/LOCAL_MANAGER_HANDOFF.md", lmh)

# 11) Vault current context / open loops.
current = read("vault/agent/CURRENT_CONTEXT.md")
current = current.replace("last_verified: 2026-08-27", "last_verified: 2026-08-28")
current = replace_required(
    current,
    "AŞAMA 6 ve AŞAMA 7 tamamlandı. 8A da **UXIA-v0 / D-068** ile tamamlandı: primary semantic shell `Today · Learn · Progress · Profile`; Assessment/English/AI/remediation contextual; shared Skill detail + PDT-v0 explanation ownership preserved. Son tamamlanan adım **8A — Bilgi mimarisi**. Aktif adım **8B — Ana ekran**; henüz yürütülmedi. 8B başlamadan fresh PRE-STEP + kullanıcı açık onayı zorunludur.",
    "AŞAMA 6 ve AŞAMA 7 tamamlandı. 8A **UXIA-v0 / D-068** ve 8B **THUX-v0 / D-069** ile tamamlandı. Today action-first canonical planner/state projection'ıdır; queue current selected PlannedTasks, capacity hard time budget, reasons PDT-v0 trace-derived, completion != mastery, missed-day debt yok, assessment/English contextual. Son tamamlanan adım **8B — Ana ekran**. Aktif adım **8C — Günlük çalışma akışı**; henüz yürütülmedi. 8C başlamadan fresh PRE-STEP + kullanıcı açık onayı zorunludur.",
    "vault CURRENT_CONTEXT execution",
)
if "- `THUX-v0 / D-069`" not in current:
    marker = "- `TEPM-v0 / D-067`: Technical English mastery/profile projection; 8 derived Skill presentation state, qualified A1/A2/B1 profile, B2+ per-capability evidence; no broad/general/official CEFR or numeric aggregate.\n"
    if marker in current:
        current = current.replace(marker, marker + "- `UXIA-v0 / D-068`: Today/Learn/Progress/Profile semantic information architecture.\n- `THUX-v0 / D-069`: action-first Today/Home contract; current PlannedTask queue + hard-capacity/reason/empty/degraded semantics.\n", 1)
write("vault/agent/CURRENT_CONTEXT.md", current)

loops = read("vault/agent/OPEN_LOOPS.md")
loops = loops.replace("last_reviewed: 2026-08-27", "last_reviewed: 2026-08-28")
loops = replace_required(
    loops,
    "- [ ] 8B Ana ekran **AKTİF**: UXIA-v0 `today` ownership üzerinden Today/home content hierarchy ve next-action UX.",
    "- [x] 8B Ana ekran: THUX-v0 / D-069 ile tamamlandı; action-first Today hierarchy + current PlannedTask queue + capacity/reason/empty/degraded semantics.\n- [ ] 8C Günlük çalışma akışı **AKTİF**: THUX-v0 Home entry/return + UXIA-v0 focused Task Runner ownership üzerinden daily task/session choreography.",
    "vault OPEN_LOOPS 8B",
)
write("vault/agent/OPEN_LOOPS.md", loops)

# 12) Progress log.
progress = read("docs/PROGRESS_LOG.md")
if "## 2026-08-28 — 8B Ana ekran tamamlandı — THUX-v0 / D-069" not in progress:
    progress = progress.rstrip() + """


## 2026-08-28 — 8B Ana ekran tamamlandı — THUX-v0 / D-069

- Kullanıcının açık onayı sonrası 8A'nın kalan D-050 stale mirror'u kapatıldı, PR #10 main'e merge edildi ve fresh 8B PRE main üzerinden yapıldı; living execution seti 8A complete / 8B active-not-executed olarak tutarlı bulundu.
- 8B separate external Research AI istemedi: yeni algorithm/technology/literature kararı değil, accepted product + UXIA-v0 + planner/capacity/prerequisite/explainability + assessment/English contracts üzerinde internal UX synthesis idi. Independent QA zorunlu tutuldu.
- Today/Home action-first semantic hierarchy tanımlandı: primary action, day plan context, current remaining PlannedTask sequence, conditional attention ve progressive supporting navigation.
- Current-day capacity override → immediate replan; capacity progress/mastery değildir ve removed work failure/debt olmaz.
- Queue yalnız selected/current PlannedTask'lardan oluşur; LearningNeed/TaskCandidate universe veya old-day backlog UI'ya sızmaz; blocked dependent work startable değildir.
- PDT-v0 bounded reason projection, contextual assessment/Technical English, SRR missed-day behavior, truthful empty/offline/AI-degraded/data-recovery states ve no-completion-to-mastery guards kilitlendi.
- İlk independent validator turu 89/90 geçti; tek fail research-decision cümlesindeki brittle exact-text marker idi. Semantik karar değişmeden wording netleştirildi; ikinci tur **90/90 PASS** oldu.
- External-memory + final Stage 6 + accepted Stage 7 + accepted 8A regressions PASS.
- D-050 POST living-memory accepted state'i `8B ✅ / 8C active-not-executed` konumuna taşır ve repo-wide stale-reference audit final closure gate'idir.

**Sonraki kesin adım:** `8C — Günlük çalışma akışı`. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
"""
write("docs/PROGRESS_LOG.md", progress)

# 13) Durable session log.
session_rel = "vault/agent/session-logs/2026-08-28-8b-today-home.md"
if not (ROOT / session_rel).exists():
    write(session_rel, """---
type: session-log
status: completed
stage_step: 8B
model: THUX-v0
decision: D-069
date: 2026-08-28
---

# 8B — Today / Home UX

Fresh PRE main üzerinde 8A complete / 8B active-not-executed state'ini doğruladı. Kullanıcı 8B execution için açık onay verdi.

## Result
- Canonical: `docs/TODAY_HOME_SCREEN_SPEC.md`
- Machine-readable: `ux/8b_today_home/home.yaml`
- QA: `ux/8b_today_home/qa_report.yaml`
- Research/synthesis: `research/8b_today_home_research.md`
- Final: `THUX-v0 / D-069`
- Independent QA: 90/90 PASS
- Stage 6 / Stage 7 / 8A / external-memory regressions: PASS

## Durable decisions
Today is action-first planner/state projection; queue only selected PlannedTasks; capacity is hard time budget; reasons are PDT-v0 trace-derived; task completion is not mastery; missed day is not debt; assessment and Technical English are contextual; offline/AI-degraded deterministic core remains usable where local capability exists.

## Next
8C — Günlük çalışma akışı is active-not-executed after POST. Fresh PRE + explicit user approval required before execution.
""")

print("8B_POST_FINALIZER=PASS")
