from __future__ import annotations

from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
PATH = ROOT / "docs/LOCAL_MANAGER_HANDOFF.md"

text = PATH.read_text(encoding="utf-8")

# Remove/advance every unqualified current-state snapshot that still says 7D is active.
replacements = {
    "- AŞAMA 7D 🟡 active-not-executed\n- 7E–20 ⬜": "- AŞAMA 7D ✅ TEIP-v0 / D-066\n- AŞAMA 7E 🟡 active-not-executed\n- 8–20 ⬜",
    "7D 🟡 ACTIVE — NOT EXECUTED\n7E–20 ⬜": "7D ✅ TEIP-v0 / D-066\n7E 🟡 ACTIVE — NOT EXECUTED\n8–20 ⬜",
    "**Sıradaki gerçek numbered work:** `7D — Teknik entegrasyon`.\n\n**7D henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**": "**Sıradaki gerçek numbered work:** `7E — English mastery`.\n\n**7E henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
    "Current active numbered step 7D'dir; fresh PRE + kullanıcı açık onayı gerekir.": "Current active numbered step 7E'dir; fresh PRE + kullanıcı açık onayı gerekir.",
}
for old, new in replacements.items():
    text = text.replace(old, new)

# Normalize any remaining simple stage-map form without touching historical prose that merely names 7D.
text = re.sub(
    r"(?m)^- AŞAMA 7D 🟡 active-not-executed$",
    "- AŞAMA 7D ✅ TEIP-v0 / D-066\n- AŞAMA 7E 🟡 active-not-executed",
    text,
)
text = re.sub(
    r"(?m)^7D 🟡 ACTIVE — NOT EXECUTED$",
    "7D ✅ TEIP-v0 / D-066\n7E 🟡 ACTIVE — NOT EXECUTED",
    text,
)

# Ensure the canonical completion addendum exists after all rewriting.
marker = "## 7D completion addendum — D-066"
if marker not in text:
    if not text.endswith("\n"):
        text += "\n"
    text += """

---

## 7D completion addendum — D-066

7D `TEIP-v0 — Technical English Integration Policy` ile tamamlandı. Canonical: `docs/TECHNICAL_ENGLISH_INTEGRATION_SPEC.md`; policy/QA: `curriculum/english/7d_technical_integration/`. Exact 15 D01 Skill korunur; 4 construct-aware mode, technical/English component attribution, bidirectional contamination guard, evidence-driven reversible scaffold ve authentic-resource integrity semantics kabul edildi. English/CEFR global technical gate değildir. Independent QA 49/49 PASS. Current active numbered step 7E'dir; fresh PRE + kullanıcı açık onayı gerekir.
"""

PATH.write_text(text, encoding="utf-8")
print("7D_LIVING_HANDOFF_FIX=PASS")
