from __future__ import annotations

from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
PATH = ROOT / "docs/LOCAL_MANAGER_HANDOFF.md"
text = PATH.read_text(encoding="utf-8")

# The handoff is a living bootstrap document. Its final takeover snapshot must advance,
# even though older completion addenda may describe the state that existed at their time.
old_final = """# 33. Final takeover state

Bu living handoff'un current canonical execution özeti:

```text
AŞAMA 1–6 ✅
7A ✅ EED-v0 / D-063
7B ✅ TECP-v0 / D-064
7C ✅ DECP-v0 / D-065
7D ✅ TEIP-v0 / D-066
7E ✅ TEPM-v0 / D-067
8A ✅ UXIA-v0 / D-068 ACTIVE — NOT EXECUTED
8–20 ⬜
```

7C final:
- Technical English common daily capacity içinde parallel candidate opportunity olarak çalışır,
- fixed daily minute/percentage/completion/streak/debt yok,
- PBR-v0 balance/starvation semantics'i reuse edilir,
- task mix exact Skill/Objective state'inden türetilir,
- spacing RVR-v0'da kalır,
- feedback-assisted revision independent mastery evidence değildir,
- technical integration 7D'ye ve learner-facing English mastery/CEFR behavior 7E'ye bırakılmıştır.

**Sıradaki gerçek numbered work:** `8B — Ana ekran`.

**8B henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**
"""
new_final = """# 33. Final takeover state

Bu living handoff'un current canonical execution özeti:

```text
AŞAMA 1–6 ✅
AŞAMA 7 ✅ — EED-v0 → TECP-v0 → DECP-v0 → TEIP-v0 → TEPM-v0
8A ✅ UXIA-v0 / D-068
8B ✅ THUX-v0 / D-069
8C 🟡 active-not-executed
8D–20 ⬜
```

8B final:
- Today/Home canonical planner/state projection'ıdır; ikinci planner değildir,
- dominant valid action + current selected PlannedTask queue kullanır,
- capacity hard time budget context'idir; progress/mastery değildir,
- PDT-v0 reason projection bounded ve trace-backed'dir,
- task completion mastery değildir; missed day debt değildir,
- assessment ve Technical English contextual kalır,
- offline/AI-degraded/recovery states truthful biçimde ayrılır,
- independent 8B QA 90/90 PASS.

**Sıradaki gerçek numbered work:** `8C — Günlük çalışma akışı`.

**8C henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**
"""
if old_final in text:
    text = text.replace(old_final, new_final, 1)
elif new_final not in text:
    raise RuntimeError("LOCAL_MANAGER_HANDOFF final takeover block anchor missing")

# 8A's addendum is historical, but this living takeover document must not retain an 8B
# active/not-executed instruction after 8B acceptance.
text = text.replace(
    "**Sıradaki gerçek numbered work:** `8B — Ana ekran`.  \n**8B henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
    "**8A sonrası:** `8B — Ana ekran` THUX-v0 / D-069 ile tamamlandı. Güncel active step için yukarıdaki Final takeover state bölümünü kullan.",
)

if "# 8B completion addendum — D-069" not in text:
    text = text.rstrip() + """


---

# 8B completion addendum — D-069

8B `THUX-v0 — Today Home UX` ile tamamlandı.

- Today action-first canonical planner/state projection,
- 5 semantic content region,
- current selected PlannedTask queue; no candidate/backlog/debt leakage,
- hard daily-capacity context + today override replan,
- PDT-v0 bounded trace-backed reasons,
- contextual assessment + Technical English,
- completion != mastery; missed day != debt,
- 12 semantic loading/ready/empty/offline/AI-degraded/recovery state,
- independent QA 90/90 PASS; Stage 6/7/8A + external-memory regressions PASS.

**Sıradaki gerçek numbered work:** `8C — Günlük çalışma akışı`.  
**8C henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**
"""

PATH.write_text(text, encoding="utf-8")

# Fail fast if any living current-state 8B instruction remains.
for pattern in [
    r"\b8B\b(?:(?![;|\n]).){0,140}(?:🟡|active-not-executed|henüz yürütülmedi|HENÜZ YÜRÜTÜLMEDİ)",
    r"(?:Aktif adım|Sıradaki gerçek numbered work)[^\n]{0,120}\b8B\b",
]:
    m = re.search(pattern, text, re.I)
    if m:
        raise RuntimeError(f"stale 8B living mirror remains: {m.group(0)}")

required = [
    "AŞAMA 8B ✅ THUX-v0 / D-069",
    "**Aktif adım:** `8C — Günlük çalışma akışı`",
    "8C 🟡 active-not-executed",
    "# 8B completion addendum — D-069",
]
missing = [x for x in required if x not in text]
if missing:
    raise RuntimeError(f"required 8B post markers missing: {missing}")

print("8B_LIVING_STATE_FIX=PASS")
