from __future__ import annotations

from pathlib import Path
import re
import yaml

ROOT = Path(__file__).resolve().parents[1]


def read(rel: str) -> str:
    return (ROOT / rel).read_text(encoding="utf-8")


def write(rel: str, text: str) -> None:
    (ROOT / rel).write_text(text, encoding="utf-8")


def replace_once(rel: str, old: str, new: str) -> None:
    text = read(rel)
    if new in text:
        return
    if old not in text:
        raise RuntimeError(f"{rel}: replacement source missing: {old[:140]!r}")
    write(rel, text.replace(old, new, 1))


def append_once(rel: str, marker: str, block: str) -> None:
    text = read(rel)
    if marker in text:
        return
    if not text.endswith("\n"):
        text += "\n"
    text += "\n" + block.strip() + "\n"
    write(rel, text)


def replace_section(rel: str, start_heading: str, next_heading: str, new_block: str) -> None:
    text = read(rel)
    if new_block.strip() in text:
        return
    pattern = re.compile(re.escape(start_heading) + r"\n.*?(?=\n" + re.escape(next_heading) + r"\n)", re.S)
    text2, n = pattern.subn(new_block.strip() + "\n", text, count=1)
    if n != 1:
        raise RuntimeError(f"{rel}: section replacement failed: {start_heading!r}")
    write(rel, text2)


# 1) Finalize canonical 7C spec and policy.
replace_once(
    "docs/DAILY_ENGLISH_COMPONENT_SPEC.md",
    "**Durum:** CANDIDATE / QA PENDING",
    "**Durum:** TAMAMLANDI",
)
replace_once(
    "docs/DAILY_ENGLISH_COMPONENT_SPEC.md",
    "**Candidate model:** `DECP-v0 — Daily English Component Policy`",
    "**Final model:** `DECP-v0 — Daily English Component Policy`",
)
replace_once(
    "docs/DAILY_ENGLISH_COMPONENT_SPEC.md",
    "**Candidate decision:** `D-065`",
    "**Final decision:** `D-065`",
)

policy_path = ROOT / "curriculum/english/7c_daily_component/policy.yaml"
policy = yaml.safe_load(policy_path.read_text(encoding="utf-8"))
policy["status"] = "accepted_7c"
policy.pop("decision_candidate", None)
policy["decision"] = "D-065"
policy_path.write_text(yaml.safe_dump(policy, sort_keys=False, allow_unicode=True, width=140), encoding="utf-8")

# 2) Permanent decision + chronological progress.
append_once(
    "docs/DECISIONS.md",
    "## D-065 — Daily English Component Policy = DECP-v0",
    """
## D-065 — Daily English Component Policy = DECP-v0
**Durum:** Kabul edildi — 2026-08-27

- 7C final modeli `DECP-v0 — Daily English Component Policy` oldu.
- Canonical spec `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`; machine-readable policy `curriculum/english/7c_daily_component/policy.yaml`; research basis `research/7c_daily_english_component_research.md`.
- Technical English ayrı daily budget/planner değildir; existing `LearningNeed → TaskCandidate → PBR-v0 → PRG-v0 → common capacity → Attempt/Artifact → Evidence` pipeline'ını kullanır.
- Fixed daily English minute, percentage/share, completion quota, streak gate, missed-day failure veya debt yoktur.
- Active study day'de open + eligible + interpretable Technical English need ve safe candidate varsa en az bir English TaskCandidate üretilir; candidate generation selection/mastery garantisi değildir.
- Normal parallel-track progress P3'tür. P0/P1 integrity/repair work dar capacity'de English'i o gün dışarıda bırakabilir.
- Repeated eligible omission yeni sabit gün eşiği yaratmadan existing PBR `track_balance_pressure` + `starvation_pressure` (`none | watch | promote`) semantics'iyle scheduling pressure üretir. Promotion eligibility/capacity/evidence guard'larını aşamaz.
- Daily task mix sabit category yüzdeleriyle değil exact Skill/Objective state'inden seçilir: new/continue learning, retention, remediation/verification, production ve reinforcement/B2+.
- Distributed/retrieval-practice research yönü kullanılır fakat 7C universal optimal interval, daily minute, percentage veya fixed checkpoint count iddia etmez. Spacing ownership RVR-v0'da kalır; empirical calibration AŞAMA 18'e aittir.
- Unknown grammar/vocabulary clean production failure üretemez; remediation exact Skill/Objective seviyesinde kalır; same failed item memorization closure değildir.
- Corrective feedback learning için kullanılabilir fakat feedback-assisted revision independent mastery evidence değildir; gerekli closure fresh H0/direct/verified attempt ister.
- B2+ yalnız TECP-v0 bounded evidence-depth extension'dır; synthetic B2+ completion veya silent Skill expansion yoktur.
- 7C yalnız English-track scaffold/cadence/task-mix behavior'ını tanımlar. Technical curriculum içindeki bilingual/English integration 7D'ye; learner-facing English mastery/CEFR behavior 7E'ye bırakılmıştır.
- Stage 6 regression + accepted 7B regression + independent 7C validator PASS.
- D-050 POST living-memory, external-memory ve repo-wide stale-reference audit ile kapanış zorunludur.

Ayrıntı: `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`.
""",
)

append_once(
    "docs/PROGRESS_LOG.md",
    "## 2026-08-27 — 7C Daily English Component tamamlandı — DECP-v0 / D-065",
    """
## 2026-08-27 — 7C Daily English Component tamamlandı — DECP-v0 / D-065

- Kullanıcı açık onayı sonrası fresh PRE-STEP ile 7B completion ve 7C active-not-executed state doğrulandı.
- Spacing/distributed practice, retrieval-practice transfer, L2 spacing ve writing feedback araştırmaları policy girdisi olarak incelendi; contradictory/conditional findings nedeniyle sabit bilimsel görünümlü daily minute/percentage/interval uydurulmadı.
- English common daily capacity içinde parallel track olarak modellendi; separate budget/quota/streak/debt yok.
- Active study day + open/eligible/safe English need durumunda en az bir candidate-generation invariant kabul edildi; selection yine PBR/PRG/capacity tarafından belirlenir.
- Existing PBR track-balance/starvation `none/watch/promote` semantics'i reuse edildi; fixed missed-day threshold eklenmedi.
- State-driven task mix, RVR spacing ownership, localized remediation, feedback/evidence safety ve TECP-v0 B2+ semantic boundary kilitlendi.
- İlk QA turunda validator PASS verdi fakat untracked `qa_report.yaml` workflow tarafından commitlenmedi; artifact-detection `git status --porcelain` ile düzeltildi ve ikinci bağımsız QA turu tamamen PASS verdi.
- D-050 POST sırasında living/current state 7C ✅ / 7D active-not-executed'e taşındı ve `LOCAL_MANAGER_HANDOFF.md` içindeki eski 6E/6F current-state drift'i temizlendi.

**Sonraki kesin adım:** `7D — Teknik entegrasyon`. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
""",
)

# 3) STEP_STATUS current state.
replace_once(
    "docs/STEP_STATUS.md",
    "| **7C — Günlük English bileşeni** | 🟡 Aktif | Daily English cadence/task-mix design; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |\n| **7D–20** | ⬜ Bekliyor | 7C sonrası canonical sırada. |",
    "| **7C — Günlük English bileşeni** | ✅ | DECP-v0 / D-065. Common capacity + daily candidate opportunity + PBR balance/starvation + state-driven task mix; QA PASS. |\n| **7D — Teknik entegrasyon** | 🟡 Aktif | English↔technical curriculum integration/scaffold behavior; henüz yürütülmedi. Fresh PRE + kullanıcı onayı gerekir. |\n| **7E–20** | ⬜ Bekliyor | 7D sonrası canonical sırada. |",
)
step = read("docs/STEP_STATUS.md")
new_tail = """## Son tamamlanan numaralı adım — 7C

**Final:** `DECP-v0 — Daily English Component Policy` / D-065.  
**Ana çıktı:** `docs/DAILY_ENGLISH_COMPONENT_SPEC.md` + `curriculum/english/7c_daily_component/`.

7C sonucu:
- Technical English common daily hard capacity içinde çalışır; separate budget yok,
- fixed daily minute/percentage/completion/streak/debt yok,
- active-study-day + open/eligible/safe English need → en az bir English TaskCandidate,
- candidate generation != selection != attempt != mastery,
- normal parallel English P3; existing PBR balance/starvation `none/watch/promote` reuse,
- fixed missed-day threshold yok,
- task mix state-driven; RVR-v0 spacing owner,
- feedback-assisted revision independent mastery evidence değil,
- TECP-v0 B2+ boundary korunuyor,
- technical integration 7D'ye, learner-facing English mastery/CEFR behavior 7E'ye deferred,
- Stage 6 + accepted 7B + independent 7C validator PASS.

## Aktif adım — 7D Teknik entegrasyon

**7D henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.
"""
step, n = re.subn(r"## Son tamamlanan numaralı adım — 7B\n.*\Z", new_tail, step, count=1, flags=re.S)
if n != 1 and new_tail not in step:
    raise RuntimeError("STEP_STATUS 7B tail not found")
write("docs/STEP_STATUS.md", step)

# 4) EXECUTION_INDEX Stage 7 and current tail.
replace_once(
    "docs/EXECUTION_INDEX.md",
    "- [ ] **7C — Günlük English bileşeni** **AKTİF**\n- [ ] **7D — Teknik entegrasyon**\n- [ ] **7E — English mastery**",
    "- [x] **7C — Günlük English bileşeni** — `DECP-v0 / D-065`\n- [ ] **7D — Teknik entegrasyon** **AKTİF**\n- [ ] **7E — English mastery**",
)
idx = read("docs/EXECUTION_INDEX.md")
idx_tail = """# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A–7C`  
**Son tamamlanan:** **`7C — DECP-v0 / D-065`**  
**Aktif:** **`7D — Teknik entegrasyon`** — active-not-executed

7C final: English common capacity içinde daily candidate opportunity olarak çalışır; fixed minute/percentage/streak/debt yok; PBR balance/starvation ve state-driven task mix kullanılır.

7D başlamadan fresh PRE-STEP GitHub refresh + kullanıcı açık onayı zorunludur.
"""
idx, n = re.subn(r"# Güncel Konum\n.*\Z", idx_tail, idx, count=1, flags=re.S)
if n != 1 and idx_tail not in idx:
    raise RuntimeError("EXECUTION_INDEX current tail not found")
write("docs/EXECUTION_INDEX.md", idx)

# 5) MASTER_PLAN Stage 7 and current tail.
replace_once(
    "docs/MASTER_PLAN.md",
    "### [ ] 7C — Günlük English bileşeni — **AKTİF**\n### [ ] 7D — Teknik entegrasyon",
    "### [x] 7C — Günlük English bileşeni — DECP-v0 / D-065\n**Final:** `docs/DAILY_ENGLISH_COMPONENT_SPEC.md` + `curriculum/english/7c_daily_component/`\n\n- common daily hard capacity; no separate English budget/quota,\n- active-study-day + open/eligible/safe English need → at least one candidate,\n- candidate/selection/attempt/mastery separation,\n- normal parallel need P3 + existing PBR track-balance/starvation reuse, no fixed omission-day threshold,\n- state-driven new/continue/retention/remediation/production/reinforcement mix,\n- RVR-v0 owns spacing; no fake universal interval/minute/percentage,\n- feedback-assisted revision != independent mastery evidence,\n- TECP-v0 B2+ semantic boundary preserved,\n- technical integration deferred to 7D; English mastery/display deferred to 7E,\n- Stage 6 + 7B regression + independent 7C validator PASS.\n\n### [ ] 7D — Teknik entegrasyon — **AKTİF**",
)
plan = read("docs/MASTER_PLAN.md")
plan_tail = """# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A–7C`  
**Son tamamlanan:** **`7C — DECP-v0 / D-065`**  
**Aktif:** **`7D — Teknik entegrasyon`** — henüz yürütülmedi.

Bir sonraki yürütme: **7D fresh PRE-STEP → EED-v0 + TECP-v0 + DECP-v0 + English Foundation Rules + technical task/prerequisite/evidence contracts ile bilingual/English technical integration tasarımı → kullanıcı onaylı execution → D-050 POST sync + stale audit.**

- D-063: 7A final `EED-v0`.
- D-064: 7B final `TECP-v0`.
- D-065: 7C final `DECP-v0`; common-capacity daily candidate opportunity + state-driven task mix + no quota/streak/debt.
"""
plan, n = re.subn(r"# Güncel Konum\n.*\Z", plan_tail, plan, count=1, flags=re.S)
if n != 1 and plan_tail not in plan:
    raise RuntimeError("MASTER_PLAN current tail not found")
write("docs/MASTER_PLAN.md", plan)

# 6) PROJECT_CONTEXT: add 7C model + current state.
ctx = read("PROJECT_CONTEXT.md")
if "### 8.3 7C Daily English Component — DECP-v0 / D-065" not in ctx:
    anchor = "Canonical: `docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md`.\n"
    if anchor not in ctx:
        raise RuntimeError("PROJECT_CONTEXT 7B canonical anchor missing")
    block = """

### 8.3 7C Daily English Component — DECP-v0 / D-065

Technical English common daily hard capacity içinde parallel track olarak çalışır. Active study day'de open + eligible + safe English need varsa en az bir candidate üretilir; selection/mastery garantisi değildir. Fixed daily minute/percentage/completion/streak/debt yoktur. PBR-v0 track-balance/starvation semantics'i reuse edilir; task mix exact Skill/Objective state'inden türetilir ve spacing RVR-v0'da kalır. Technical-task integration 7D'ye, learner-facing English mastery/CEFR behavior 7E'ye bırakılmıştır.

Canonical: `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`.
"""
    ctx = ctx.replace(anchor, anchor + block, 1)
ctx = ctx.replace(
    "- **7C 🟡 Günlük English bileşeni — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n- 7D–20 ⬜",
    "- **7C ✅ DECP-v0 / D-065 — Günlük English bileşeni tamamlandı**\n- **7D 🟡 Teknik entegrasyon — AKTİF, HENÜZ YÜRÜTÜLMEDİ**\n- 7E–20 ⬜",
)
ctx = ctx.replace(
    "**Sıradaki numaralı çalışma 7C'dir.** Fresh PRE-STEP + kullanıcı açık onayı olmadan yürütülmez.",
    "**Sıradaki numaralı çalışma 7D'dir.** Fresh PRE-STEP + kullanıcı açık onayı olmadan yürütülmez.",
)
write("PROJECT_CONTEXT.md", ctx)

# 7) HANDOFF current state and 7C/7D handoff.
handoff = read("docs/HANDOFF_STATE.md")
handoff = handoff.replace("- AŞAMA 7C 🟡 active-not-executed\n- 7D–20 ⬜", "- AŞAMA 7C ✅ — DECP-v0 / D-065\n- AŞAMA 7D 🟡 active-not-executed\n- 7E–20 ⬜")
handoff = handoff.replace(
    "**Son tamamlanan:** `7B — TECP-v0 / D-064`  \n**Aktif:** `7C — Günlük English bileşeni`  \n**7C henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
    "**Son tamamlanan:** `7C — DECP-v0 / D-065`  \n**Aktif:** `7D — Teknik entegrasyon`  \n**7D henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
)
# Replace old 7C handoff section with final 7C summary + 7D handoff.
handoff, n = re.subn(
    r"## 16\. 7C handoff\n.*\Z",
    """## 16. D-065 / 7C final özeti

Canonical: `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`.  
Policy/QA: `curriculum/english/7c_daily_component/`.  
Research: `research/7c_daily_english_component_research.md`.

DECP-v0:
- common daily capacity; separate English budget yok,
- active-study-day + open/eligible/safe English need → en az bir candidate,
- fixed minute/percentage/completion/streak/debt yok,
- normal parallel need P3 + PBR balance/starvation `none/watch/promote`,
- state-driven task mix; RVR spacing ownership,
- localized remediation + fresh variant + feedback evidence guards,
- B2+ TECP-v0 semantic boundary korunuyor,
- technical integration 7D'ye, learner-facing English mastery/CEFR behavior 7E'ye deferred,
- independent 7C QA PASS.

## 17. 7D handoff

7D — Teknik entegrasyon; EED-v0 + TECP-v0 + DECP-v0 ile English Foundation Rules / technical prerequisite / assessment evidence contracts'ını birleştirerek Python/C/Linux/GPU vb. teknik task'lerde English/bilingual scaffold'ın ne zaman ve nasıl kullanılacağını tasarlayacaktır. English global hard gate olmayacaktır. 7D fresh PRE + kullanıcı açık onayı olmadan yürütülmez.
""",
    handoff,
    count=1,
    flags=re.S,
)
if n != 1 and "## 17. 7D handoff" not in handoff:
    raise RuntimeError("HANDOFF 7C handoff tail not found")
write("docs/HANDOFF_STATE.md", handoff)

# 8) START_HERE stage map, reading list, current state and command.
start = read("docs/START_HERE.md")
start = start.replace(
    "- 7 English parallel line — **7A ✅ EED-v0 / D-063; 7B ✅ TECP-v0 / D-064; 7C 🟡 active-not-executed**",
    "- 7 English parallel line — **7A ✅ EED-v0 / D-063; 7B ✅ TECP-v0 / D-064; 7C ✅ DECP-v0 / D-065; 7D 🟡 active-not-executed**",
)
if "11y. `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`" not in start:
    start = start.replace(
        "11x. `curriculum/english/7b_cefr_progression/qa_report.yaml`",
        "11x. `curriculum/english/7b_cefr_progression/qa_report.yaml`\n11y. `docs/DAILY_ENGLISH_COMPONENT_SPEC.md`\n11z. `curriculum/english/7c_daily_component/policy.yaml`\n11aa. `curriculum/english/7c_daily_component/qa_report.yaml`",
    )
start = start.replace(
    "- AŞAMA 7: **7A ✅ EED-v0 / D-063**, **7B ✅ TECP-v0 / D-064**, **7C 🟡 active-not-executed**",
    "- AŞAMA 7: **7A ✅ EED-v0 / D-063**, **7B ✅ TECP-v0 / D-064**, **7C ✅ DECP-v0 / D-065**, **7D 🟡 active-not-executed**",
)
start = re.sub(
    r"7B final: \*\*15 D01 Skill = 5 A1 / 5 A2 / 5 B1\*\*.*?\n\n## 9\. Güncel çalışma konumu",
    "7C final: Technical English common capacity içinde daily candidate opportunity olarak çalışır; fixed minute/percentage/completion/streak/debt yoktur; PBR balance/starvation ve state-driven task mix kullanılır.\n\n## 9. Güncel çalışma konumu",
    start,
    count=1,
    flags=re.S,
)
start = re.sub(
    r"\*\*Son tamamlanan:\*\* \*\*`7B — TECP-v0 / D-064`\*\*  \n\*\*Aktif:\*\* \*\*`7C — Günlük English bileşeni`\*\*  \n\*\*7C henüz yürütülmedi\. Fresh PRE-STEP \+ kullanıcı açık onayı zorunludur\.\*\*",
    "**Son tamamlanan:** **`7C — DECP-v0 / D-065`**  \n**Aktif:** **`7D — Teknik entegrasyon`**  \n**7D henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
    start,
    count=1,
)
start = re.sub(
    r"> `xpike-dgm/ai-infra-learning-coach reposunda AGENTS\.md \+ SESSION_START.*?`$",
    "> `xpike-dgm/ai-infra-learning-coach reposunda AGENTS.md + SESSION_START + START_HERE + PROJECT_MEMORY_PROTOCOL ile başla. Current execution için EXECUTION_INDEX + STEP_STATUS + HANDOFF_STATE + PROJECT_CONTEXT + MASTER_PLAN'ı fresh çapraz doğrula. 7C, DECP-v0 / D-065 ile tamamlandı: common-capacity daily English candidate opportunity, no fixed minute/percentage/streak/debt, PBR balance/starvation, state-driven task mix. Aktif step 7D — Teknik entegrasyon; 7D henüz yürütülmedi. Numaralı adımı fresh PRE ve kullanıcı açık onayı olmadan yürütme.`",
    start,
    count=1,
    flags=re.S,
)
write("docs/START_HERE.md", start)

# 9) AGENTS current execution state.
agents = read("AGENTS.md")
agents = agents.replace(
    "- 7B: ✅ `TECP-v0 / D-064` tamamlandı — 15 Skill = 5 A1 + 5 A2 + 5 B1; 4 bounded B2+ extension; CEFR review resolved.\n- **Aktif adım: 7C — Günlük English bileşeni.**\n- **7C henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.",
    "- 7B: ✅ `TECP-v0 / D-064` tamamlandı — 15 Skill = 5 A1 + 5 A2 + 5 B1; 4 bounded B2+ extension; CEFR review resolved.\n- 7C: ✅ `DECP-v0 / D-065` tamamlandı — common-capacity daily candidate opportunity; no fixed minute/percentage/streak/debt; PBR balance/starvation + state-driven task mix.\n- **Aktif adım: 7D — Teknik entegrasyon.**\n- **7D henüz yürütülmedi.** Fresh PRE-STEP + kullanıcı açık onayı zorunludur.",
)
agents = agents.replace(
    "**7C'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 7C için ayrıca fresh PRE-STEP refresh yap ve kullanıcı açık onayını doğrula.",
    "**7D'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 7D için ayrıca fresh PRE-STEP refresh yap ve kullanıcı açık onayını doğrula.",
)
write("AGENTS.md", agents)

# 10) Vault current context/open loops.
vctx = read("vault/agent/CURRENT_CONTEXT.md")
if "DECP-v0 / D-065" not in vctx:
    vctx = vctx.replace(
        "- `TECP-v0 / D-064`: D01 CEFR-aligned Technical English progression; 5 A1 + 5 A2 + 5 B1 base anchors, 4 bounded B2+ extension; CEFR != mastery/certification.",
        "- `TECP-v0 / D-064`: D01 CEFR-aligned Technical English progression; 5 A1 + 5 A2 + 5 B1 base anchors, 4 bounded B2+ extension; CEFR != mastery/certification.\n- `DECP-v0 / D-065`: Daily Technical English common capacity içinde candidate opportunity; fixed quota/streak/debt yok; PBR balance/starvation + state-driven task mix.",
    )
vctx = re.sub(
    r"AŞAMA 6 ve 7A–7B tamamlandı\. Son tamamlanan adım \*\*7B — TECP-v0 / D-064\*\*\..*?zorunludur\.",
    "AŞAMA 6 ve 7A–7C tamamlandı. Son tamamlanan adım **7C — DECP-v0 / D-065**. 7C Technical English'i common daily capacity içinde daily candidate opportunity olarak tutar; fixed minute/percentage/completion/streak/debt yoktur. Aktif adım **7D — Teknik entegrasyon**; henüz yürütülmedi. 7D başlamadan fresh PRE-STEP + kullanıcı açık onayı zorunludur.",
    vctx,
    count=1,
    flags=re.S,
)
write("vault/agent/CURRENT_CONTEXT.md", vctx)

loops = read("vault/agent/OPEN_LOOPS.md")
loops = loops.replace(
    "- [ ] 7C Daily English component **AKTİF**: cadence, task mix, planner capacity ve pause/resume behavior tasarımı.",
    "- [x] 7C Daily English component: DECP-v0 / D-065 ile tamamlandı; common-capacity daily candidate + no quota/streak/debt + PBR balance/starvation + state-driven task mix.\n- [ ] 7D Technical integration **AKTİF**: technical task'lerde English/bilingual scaffold, construct fairness ve cross-track evidence/prerequisite behavior.",
)
write("vault/agent/OPEN_LOOPS.md", loops)

# 11) LOCAL_MANAGER_HANDOFF is living memory: remove contradictory old 6E/6F "current" snapshots.
lmh = read("docs/LOCAL_MANAGER_HANDOFF.md")
# Replace sections 21-22 in one pass, keeping section 23 onwards.
pattern = re.compile(r"# 21\. Tamamlanan proje aşamaları\n.*?(?=\n# 23\. Tamamlanan 6B'nin görevi ve final çıktısı\n)", re.S)
replacement = """# 21. Tamamlanan proje aşamaları — current summary

- AŞAMA 1 ✅ Product framing
- AŞAMA 2 ✅ Learning/mastery — GRE-v0 / RVR-v0
- AŞAMA 3 ✅ Adaptive planner — PBR/PRG/VDW/SRR/PDT + simulations PASS
- AŞAMA 4 ✅ Assessment — DMA/WBA/MCA/QAB/AIV
- AŞAMA 5 ✅ Curriculum/knowledge-graph backbone
- AŞAMA 6 ✅ Granular Capability Map + external Research QA — S6ERQA-v0 / D-062
- AŞAMA 7A ✅ EED-v0 / D-063
- AŞAMA 7B ✅ TECP-v0 / D-064
- AŞAMA 7C ✅ DECP-v0 / D-065
- AŞAMA 7D 🟡 active-not-executed
- 7E–20 ⬜

# 22. Current exact state — en kritik takeover bilgisi

**Son tamamlanan numaralı adım:** `7C — Günlük English bileşeni`  
**Final:** `DECP-v0 — Daily English Component Policy` / D-065  
**Canonical:** `docs/DAILY_ENGLISH_COMPONENT_SPEC.md` + `curriculum/english/7c_daily_component/`

**Aktif adım:** `7D — Teknik entegrasyon`  
**Durum:** **HENÜZ YÜRÜTÜLMEDİ**

7D, technical task içindeki English/bilingual scaffold ve cross-track prerequisite/evidence fairness davranışını tasarlayacaktır. English global technical hard gate olmayacaktır.

Kullanıcı 7D'yi onayladığında:

```text
fresh 7D PRE-STEP GitHub refresh
→ 7D execution
→ independent QA
→ D-050 POST sync
→ repo-wide stale-reference audit
→ 7D completed; 7E active-not-executed
```
"""
lmh2, n = pattern.subn(replacement, lmh, count=1)
if n != 1:
    raise RuntimeError("LOCAL_MANAGER_HANDOFF sections 21-22 not found")
lmh = lmh2
# Genericize old section 30 instruction.
lmh = lmh.replace(
    "Bu sorulardan biri belirsizse, 6E'ye başlamadan ilgili canonical dosya yeniden okunmalıdır.",
    "Bu sorulardan biri belirsizse, aktif numaralı adıma başlamadan ilgili canonical dosya yeniden okunmalıdır.",
)
# Replace old step-specific takeover prompt in section 32.
lmh, n = re.subn(
    r"# 32\. Local agent'a verilecek kısa takeover komutu\n.*?(?=\n---\n\n# 33\. Final takeover state\n)",
    """# 32. Local agent'a verilecek kısa takeover komutu

Kullanıcı yeni local-manager oturumunda şu durable promptu kullanabilir:

> **Bu reponun ana proje yöneticisisin. Önce root `AGENTS.md`, `vault/agent/SESSION_START.md`, `docs/LOCAL_MANAGER_HANDOFF.md`, `docs/START_HERE.md` ve `docs/PROJECT_MEMORY_PROTOCOL.md` dosyalarını oku. Current execution'ı `EXECUTION_INDEX + STEP_STATUS + HANDOFF_STATE + PROJECT_CONTEXT + MASTER_PLAN` ile fresh çapraz doğrula. Canonical decisions/specs ile historical/noncanonical notları ayır. Current active numbered step'i fresh PRE-STEP ve kullanıcı açık onayı olmadan yürütme. Her numbered step sonunda D-050 living-memory sync + repo-wide stale-reference audit uygula.**
""",
    lmh,
    count=1,
    flags=re.S,
)
if n != 1:
    raise RuntimeError("LOCAL_MANAGER_HANDOFF section 32 not found")
# Replace final takeover state through EOF; handoff is living/current, not historical log.
lmh, n = re.subn(
    r"# 33\. Final takeover state\n.*\Z",
    """# 33. Final takeover state

Bu living handoff'un current canonical execution özeti:

```text
AŞAMA 1–6 ✅
7A ✅ EED-v0 / D-063
7B ✅ TECP-v0 / D-064
7C ✅ DECP-v0 / D-065
7D 🟡 ACTIVE — NOT EXECUTED
7E–20 ⬜
```

7C final:
- Technical English common daily capacity içinde parallel candidate opportunity olarak çalışır,
- fixed daily minute/percentage/completion/streak/debt yok,
- PBR-v0 balance/starvation semantics'i reuse edilir,
- task mix exact Skill/Objective state'inden türetilir,
- spacing RVR-v0'da kalır,
- feedback-assisted revision independent mastery evidence değildir,
- technical integration 7D'ye ve learner-facing English mastery/CEFR behavior 7E'ye bırakılmıştır.

**Sıradaki gerçek numbered work:** `7D — Teknik entegrasyon`.  
**7D henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**
""",
    lmh,
    count=1,
    flags=re.S,
)
if n != 1:
    raise RuntimeError("LOCAL_MANAGER_HANDOFF section 33 not found")
write("docs/LOCAL_MANAGER_HANDOFF.md", lmh)

print("7C_POST_FINALIZER=PASS")
