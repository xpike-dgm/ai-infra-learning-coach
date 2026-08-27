from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]

# START_HERE has a second current-progress/bootstrap block after the stage mapping.
p = ROOT / "docs/START_HERE.md"
t = p.read_text(encoding="utf-8")
t = t.replace(
    "11o. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`",
    "11o. `docs/STAGE6_EXTERNAL_RESEARCH_QA.md`\n11p. `research/6h_external_research_ai_report.md`\n11q. `curriculum/decomposition/6h_research_qa/final_qa_report.yaml`\n11r. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`",
)
final_tail = """## 8. Tamamlanan çekirdek

- AŞAMA 1 ✅ Product framing
- AŞAMA 2 ✅ GRE-v0 + RVR-v0 learning/mastery
- AŞAMA 3 ✅ Adaptive planner — 16/16 scenarios, 20/20 invariants
- AŞAMA 4 ✅ DMA/WBA/MCA/QAB/AIV assessment system
- AŞAMA 5 ✅ PDM-v0 / KGC-v0 / FBB-v0 / GQA-v0
- AŞAMA 6 ✅ GNS-v0 / FRDB-v0 / FDM-v0 / SDM-v0 / GIM-v0 / PEM-v0 / WLRM-v0 / **S6ERQA-v0 / D-062**

Final Stage 6: **23 route / 549 Skill / 608 Objective / 950 prerequisite edge / 549/549 hard DAG**. WLRM exact coverage 549/608; 10/10 6H review resolved.

## 9. Güncel çalışma konumu

**Son tamamlanan:** **`6H — S6ERQA-v0 / D-062`**  
**Aktif:** **`7A — İngilizce başlangıç ölçümü`**  
**7A henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**

## 10. Yeni sohbet için kısa komut
> `xpike-dgm/ai-infra-learning-coach reposunda AGENTS.md + SESSION_START + START_HERE + PROJECT_MEMORY_PROTOCOL ile başla. Current execution için EXECUTION_INDEX + STEP_STATUS + HANDOFF_STATE + PROJECT_CONTEXT + MASTER_PLAN'ı fresh çapraz doğrula. Stage 6, S6ERQA-v0 / D-062 ile tamamlandı; final graph 549 Skill / 608 Objective / 950 edge ve 549/549 hard DAG. Aktif step 7A — İngilizce başlangıç ölçümü; 7A henüz yürütülmedi. Numaralı adımı fresh PRE ve kullanıcı açık onayı olmadan yürütme.`
"""
t, n = re.subn(r"## 8\. Tamamlanan çekirdek\n.*\Z", final_tail, t, count=1, flags=re.S)
if n != 1:
    raise RuntimeError("START_HERE second current block not found")
p.write_text(t, encoding="utf-8")

# EXECUTION_INDEX has a bottom current-state mirror; synchronize it.
p = ROOT / "docs/EXECUTION_INDEX.md"
t = p.read_text(encoding="utf-8")
final_tail = """# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`  
**Son tamamlanan:** **`6H — S6ERQA-v0 / D-062`**  
**Aktif:** **`7A — Başlangıç ölçümü`** — active-not-executed

6H external reconciliation sonrası Stage 6 final registry **549 Skill / 608 Objective / 950 prerequisite edge**; combined hard graph **549/549 DAG** ve 10/10 6H review resolved.

7A başlamadan fresh PRE-STEP GitHub refresh + kullanıcı açık onayı zorunludur.

- D-060: 6F final Professional Engineering / Projects detailed map `PEM-v0`.
- D-061: 6G final weakness localization/remediation overlay `WLRM-v0`; final Stage 6 registry için 549 Skill + 608 Objective exact coverage.
- D-062: 6H final external Research QA `S6ERQA-v0`; canonical `docs/STAGE6_EXTERNAL_RESEARCH_QA.md`.
"""
t, n = re.subn(r"# Güncel Konum\n.*\Z", final_tail, t, count=1, flags=re.S)
if n != 1:
    raise RuntimeError("EXECUTION_INDEX current tail not found")
p.write_text(t, encoding="utf-8")

# MASTER_PLAN bottom current mirror was stale even before 6H; replace entirely.
p = ROOT / "docs/MASTER_PLAN.md"
t = p.read_text(encoding="utf-8")
final_tail = """# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`  
**Son tamamlanan:** **`6H — S6ERQA-v0 / D-062`**  
**Aktif:** **`7A — İngilizce başlangıç ölçümü`** — henüz yürütülmedi.

Bir sonraki yürütme: **7A fresh PRE-STEP → Stage 6 Technical English capability map + English foundation rules + assessment/mastery contracts ile başlangıç ölçümü tasarımı → kullanıcı onaylı execution → D-050 POST sync + stale audit.**

- D-060: 6F final `PEM-v0`.
- D-061: 6G final `WLRM-v0`; final external-QA registry 549 Skill / 608 Objective coverage.
- D-062: 6H final `S6ERQA-v0`; 3 independent evaluator reconciliation, 950 edges, 549/549 hard DAG, 10/10 review closure.
"""
t, n = re.subn(r"# Güncel Konum\n.*\Z", final_tail, t, count=1, flags=re.S)
if n != 1:
    raise RuntimeError("MASTER_PLAN current tail not found")
p.write_text(t, encoding="utf-8")

# LOCAL_MANAGER_HANDOFF may carry more than one old snapshot string.
p = ROOT / "docs/LOCAL_MANAGER_HANDOFF.md"
t = p.read_text(encoding="utf-8")
t = t.replace("6A–6G completed / 6H active-not-executed", "6A–6H completed / 7A active-not-executed")
t = t.replace("6H active-not-executed", "6H completed; 7A active-not-executed")
t = t.replace("6H henüz yürütülmedi", "6H D-062 ile tamamlandı")
t = t.replace("Aktif adım 6H", "Son tamamlanan adım 6H; aktif adım 7A")
p.write_text(t, encoding="utf-8")

# Separate completed 6H fact from the 7A not-executed sentence so stale scanners do not conflate them.
p = ROOT / "vault/agent/CURRENT_CONTEXT.md"
t = p.read_text(encoding="utf-8")
t = t.replace("10/10 6H review resolved. Aktif adım", "10/10 6H review resolved.\n\nAktif adım")
p.write_text(t, encoding="utf-8")

print("6H_POST_STALE_FIX=PASS")
