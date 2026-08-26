from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def update(path: str, replacements: list[tuple[str, str]]) -> None:
    p = ROOT / path
    text = p.read_text(encoding="utf-8")
    for old, new in replacements:
        text = text.replace(old, new)
    p.write_text(text, encoding="utf-8")


update("docs/START_HERE.md", [
    ("Şu an aktif adım 6F — Professional engineering / project map; 6F henüz yürütülmedi.",
     "Şu an aktif adım 6G — Weakness localization + remediation mapping; 6G henüz yürütülmedi."),
    ("11k. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`", "11m. `docs/GRANULAR_CAPABILITY_MAP_PLAN.md`"),
])

update("docs/LOCAL_MANAGER_HANDOFF.md", [
    ("fresh 6F PRE-STEP GitHub refresh", "fresh 6G PRE-STEP GitHub refresh"),
    ("→ 6F execution", "→ 6G execution"),
    ("→ 6F active-not-executed", "→ 6G active-not-executed"),
])

print("POST_6F_EXTRA_SYNC=PASS")
