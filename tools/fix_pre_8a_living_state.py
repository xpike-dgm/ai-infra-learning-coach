from __future__ import annotations

from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]


def replace_once(path: Path, old: str, new: str, label: str) -> None:
    text = path.read_text(encoding="utf-8")
    if old in text:
        text = text.replace(old, new, 1)
        path.write_text(text, encoding="utf-8")
        return
    if new in text:
        return
    raise RuntimeError(f"{label}: neither old nor expected new text found in {path}")


handoff = ROOT / "docs/HANDOFF_STATE.md"
text = handoff.read_text(encoding="utf-8")

# Normalize the duplicated Stage-7/8A transition block left by the 7E POST cleanup.
text = text.replace(
    "- AŞAMA 7E ✅ TEPM-v0 / D-067\n8A 🟡 active-not-executed\n- 8A 🟡 active-not-executed\n8B–20 ⬜",
    "- AŞAMA 7 ✅ — EED-v0 / D-063 → TECP-v0 / D-064 → DECP-v0 / D-065 → TEIP-v0 / D-066 → TEPM-v0 / D-067\n- 8A 🟡 active-not-executed\n- 8B–20 ⬜",
)

# Replace the stale current-state section while preserving all historical completion prose.
text, n = re.subn(
    r"## 11\. Güncel kesin konum\n\n\*\*Son tamamlanan:\*\* `7D — TEIP-v0 / D-066`  \n\*\*Aktif:\*\* `7E — English mastery`  \n\*\*8A henüz yürütülmedi\. Fresh PRE-STEP \+ kullanıcı açık onayı zorunludur\.\*\*",
    "## 11. Güncel kesin konum\n\n**Son tamamlanan:** `7E — TEPM-v0 / D-067`  \n**AŞAMA 7:** ✅ TAMAMLANDI  \n**Aktif:** `8A — Bilgi mimarisi`  \n**8A henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
    text,
    count=1,
)
if n == 0 and "**Son tamamlanan:** `7E — TEPM-v0 / D-067`" not in text:
    raise RuntimeError("HANDOFF_STATE current-state replacement anchor missing")

handoff.write_text(text, encoding="utf-8")

local = ROOT / "docs/LOCAL_MANAGER_HANDOFF.md"
local_text = local.read_text(encoding="utf-8")
local_text = local_text.replace(
    "8. Current execution state'in `6A–6H completed / 7A–7B completed / 7C active-not-executed` olduğunu doğrula.\n9. Ancak bundan sonra, 7C için **ayrı bir fresh PRE-STEP GitHub refresh** yap; kullanıcı açık onayı olmadan yürütme.",
    "8. Current execution state'in `AŞAMA 6 ✅ / AŞAMA 7 ✅ / 8A active-not-executed` olduğunu living-memory setiyle doğrula.\n9. Ancak bundan sonra, aktif numbered step için **ayrı bir fresh PRE-STEP GitHub refresh** yap; kullanıcı açık onayı olmadan yürütme.",
)
local.write_text(local_text, encoding="utf-8")

# Assertions: PRE-8A current state must be unambiguous in living handoff sources.
for rel in ["docs/HANDOFF_STATE.md", "docs/LOCAL_MANAGER_HANDOFF.md"]:
    data = (ROOT / rel).read_text(encoding="utf-8")
    forbidden = [
        "**Son tamamlanan:** `7D — TEIP-v0 / D-066`",
        "**Aktif:** `7E — English mastery`",
        "7C active-not-executed",
        "7C için **ayrı bir fresh PRE-STEP",
    ]
    hits = [x for x in forbidden if x in data]
    if hits:
        raise RuntimeError(f"{rel}: stale current-state strings remain: {hits}")

handoff_data = handoff.read_text(encoding="utf-8")
required_handoff = [
    "**Son tamamlanan:** `7E — TEPM-v0 / D-067`",
    "**AŞAMA 7:** ✅ TAMAMLANDI",
    "**Aktif:** `8A — Bilgi mimarisi`",
    "**8A henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
]
for marker in required_handoff:
    if marker not in handoff_data:
        raise RuntimeError(f"HANDOFF_STATE required marker missing: {marker}")

print("PRE_8A_LIVING_STATE_FIX=PASS")
