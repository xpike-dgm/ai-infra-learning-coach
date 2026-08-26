from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def replace_required(path: str, old: str, new: str) -> None:
    p = ROOT / path
    text = p.read_text(encoding="utf-8")
    if old not in text:
        raise SystemExit(f"POST_6G_EXTRA_FAIL {path}: missing {old!r}")
    p.write_text(text.replace(old, new, 1), encoding="utf-8")


replace_required(
    "docs/START_HERE.md",
    "  - 6G 🟡 Weakness localization + remediation mapping\n  - 6H ⬜",
    "  - 6G ✅ WLRM-v0 / D-061\n  - 6H 🟡 Coverage / prerequisite / Research QA",
)

replace_required(
    "AGENTS.md",
    "- **Aktif adım: 6G — Weakness localization + remediation mapping.**\n- **6G henüz yürütülmedi.**\n- 6H ve AŞAMA 7–20 bekliyor.\n\n**6G'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 6G için ayrıca fresh PRE-STEP refresh yap.",
    "- AŞAMA 6G: ✅ `WLRM-v0 / D-061` tamamlandı.\n- **Aktif adım: 6H — Coverage / prerequisite / Research QA.**\n- **6H henüz yürütülmedi; bağımsız external Research AI zorunludur.**\n- AŞAMA 7–20 bekliyor.\n\n**6H'yi bu dosyayı okuyarak doğrudan başlatma.** Önce takeover/current-state okumasını tamamla, sonra 6H için ayrıca fresh PRE-STEP refresh yap ve bağımsız external Research AI workflow'unu doğrula.",
)

print("POST_6G_EXTRA_SYNC=PASS")
