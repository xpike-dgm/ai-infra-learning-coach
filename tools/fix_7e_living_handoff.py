from __future__ import annotations

from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]


def rewrite(rel: str) -> None:
    path = ROOT / rel
    text = path.read_text(encoding="utf-8")

    # Advance explicit stage-map/current-state forms from 7E -> 8A. Keep completed
    # 7E and active 8A on separate lines so the state is unambiguous to both people
    # and deterministic stale-reference scanners.
    text = text.replace(
        "7E 🟡 active-not-executed",
        "7E ✅ TEPM-v0 / D-067\n8A 🟡 active-not-executed",
    )
    text = re.sub(
        r"(?m)^([\-* ]*)7E 🟡(?: ACTIVE)?(?: — NOT EXECUTED)?$",
        r"\g<1>7E ✅ TEPM-v0 / D-067\n\g<1>8A 🟡 ACTIVE — NOT EXECUTED",
        text,
    )

    # Current-range shorthand: 8A is active, so future range begins at 8B.
    text = text.replace("7E–20 ⬜", "8A 🟡 active-not-executed\n8B–20 ⬜")
    text = text.replace("7E–20", "8B–20")

    # If an earlier cleanup already created the semicolon form, normalize it too.
    text = text.replace(
        "7E ✅ TEPM-v0 / D-067; 8A 🟡 active-not-executed",
        "7E ✅ TEPM-v0 / D-067\n8A 🟡 active-not-executed",
    )
    text = text.replace(
        "8A 🟡 active-not-executed; 8B–20 ⬜",
        "8A 🟡 active-not-executed\n8B–20 ⬜",
    )

    # Move current next-work statements to 8A. Keep historical prose that only
    # discusses 7E design semantics; these replacements target current-state wording.
    text = re.sub(
        r"(\*\*Sıradaki gerçek numbered work:\*\*\s*`)7E\s*[—-]\s*English mastery(`)",
        r"\g<1>8A — Bilgi mimarisi\g<2>",
        text,
    )
    text = re.sub(
        r"(\*\*Sıradaki gerçek numbered work:\*\*\s*`)7E([^`]*`)",
        r"\g<1>8A — Bilgi mimarisi\g<2>",
        text,
    )
    text = re.sub(
        r"(?i)(Sıradaki numaralı çalışma[^\n]{0,40})7E\b",
        r"\g<1>8A",
        text,
    )

    # Replace unqualified current-state "7E not executed" clauses. Historical
    # execution logs are not in these living handoff files' current-state blocks.
    text = re.sub(
        r"\*\*7E henüz yürütülmedi\. Fresh PRE-STEP \+ kullanıcı açık onayı zorunludur\.\*\*",
        "**8A henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur.**",
        text,
    )
    text = re.sub(
        r"\b7E henüz yürütülmedi\b",
        "8A henüz yürütülmedi",
        text,
    )
    text = re.sub(
        r"\b7E HENÜZ YÜRÜTÜLMEDİ\b",
        "8A HENÜZ YÜRÜTÜLMEDİ",
        text,
    )

    # Normalize any simple current active-step clauses that survived formatting differences.
    text = re.sub(
        r"(?i)(Aktif adım|Aktif step)([^\n]{0,30})7E\s*[—-]\s*English mastery",
        r"\1\2 8A — Bilgi mimarisi",
        text,
    )

    path.write_text(text, encoding="utf-8")


for rel in ["docs/HANDOFF_STATE.md", "docs/LOCAL_MANAGER_HANDOFF.md"]:
    rewrite(rel)

# Assert final/current markers so cleanup cannot silently succeed with wrong state.
handoff = (ROOT / "docs/HANDOFF_STATE.md").read_text(encoding="utf-8")
local = (ROOT / "docs/LOCAL_MANAGER_HANDOFF.md").read_text(encoding="utf-8")

required = {
    "docs/HANDOFF_STATE.md": [
        "D-067 / 7E final özeti",
        "8A handoff",
    ],
    "docs/LOCAL_MANAGER_HANDOFF.md": [
        "7E completion addendum — D-067",
        "8A — Bilgi mimarisi",
    ],
}
for rel, markers in required.items():
    data = handoff if rel.endswith("HANDOFF_STATE.md") else local
    for marker in markers:
        if marker not in data:
            raise RuntimeError(f"{rel}: required post-7E marker missing: {marker}")

print("7E_LIVING_HANDOFF_FIX=PASS")
