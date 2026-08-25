from pathlib import Path


def read(path):
    return Path(path).read_text(encoding="utf-8")


def write(path, text):
    Path(path).write_text(text, encoding="utf-8")


# The base sync helper intentionally stops when it sees the old MASTER_PLAN
# current-position note. Convert that living block now; older D-050/PROGRESS_LOG
# lines remain historical provenance.
p = Path("docs/MASTER_PLAN.md")
text = read(p)
old = """**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A`  
**Aktif:** **`5B — Graph / Topic metadata sözleşmesi`**

Bir sonraki yürütme: **5B başlamadan yeni PRE-STEP GitHub refresh → graph/metadata contract → POST-STEP D-050 sync + stale-reference audit.**"""
new = """**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5B`  
**Aktif:** **`5C — İlk 8–12 haftalık curriculum backbone`**

Bir sonraki yürütme: **5C başlamadan yeni PRE-STEP GitHub refresh → V1 başlangıç curriculum backbone → POST-STEP D-050 sync + stale-reference audit.**"""
if old not in text:
    raise SystemExit("MASTER_PLAN current-position block not found")
write(p, text.replace(old, new, 1))

checks = {
    "PROJECT_CONTEXT.md": ["5B ✅ KGC-v0 / D-051", "5C 🟡", "AKTİF, HENÜZ YÜRÜTÜLMEDİ"],
    "docs/START_HERE.md": ["5B ✅ `KGC-v0", "5C 🟡", "5C henüz yürütülmedi"],
    "docs/HANDOFF_STATE.md": ["5B ✅ KGC-v0 / D-051", "5C 🟡", "5C henüz yürütülmedi"],
    "docs/STEP_STATUS.md": ["5B — Graph / Topic metadata sözleşmesi** | ✅", "5C — İlk 8–12 haftalık curriculum backbone** | 🟡 Aktif", "5C henüz yürütülmedi"],
    "docs/EXECUTION_INDEX.md": ["[x] **5B — Graph / Topic metadata sözleşmesi**", "[ ] **5C — İlk 8–12 haftalık curriculum backbone** **AKTİF**"],
    "docs/MASTER_PLAN.md": ["[x] 5B — Graph / Topic metadata sözleşmesi — KGC-v0 / D-051", "[ ] 5C — İlk 8–12 haftalık curriculum backbone — **AKTİF**", "**Aktif:** **`5C — İlk 8–12 haftalık curriculum backbone`**"],
}
for path, needles in checks.items():
    body = read(path)
    for needle in needles:
        if needle not in body:
            raise SystemExit(f"{path}: missing {needle!r}")

strict_living = [
    "PROJECT_CONTEXT.md", "docs/START_HERE.md", "docs/HANDOFF_STATE.md",
    "docs/STEP_STATUS.md", "docs/EXECUTION_INDEX.md", "docs/MASTER_PLAN.md",
]
forbidden = [
    "5B 🟡 Graph / Topic metadata sözleşmesi",
    "5B henüz yürütülmedi",
    "**Aktif:** **`5B — Graph / Topic metadata sözleşmesi`**",
    "### [ ] 5B — Graph / Topic metadata sözleşmesi — **AKTİF**",
]
for path in strict_living:
    body = read(path)
    for needle in forbidden:
        if needle in body:
            raise SystemExit(f"{path}: stale living 5B marker {needle!r}")

if "## D-051 — Curriculum knowledge graph contract = KGC-v0" not in read("docs/DECISIONS.md"):
    raise SystemExit("D-051 missing")
kgc = read("docs/CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md")
if "KGC-v0" not in kgc or "**Durum:** TAMAMLANDI" not in kgc:
    raise SystemExit("KGC canonical output incomplete")

pointer_files = [
    "README.md", "PROJECT_CONTEXT.md", "docs/START_HERE.md", "docs/HANDOFF_STATE.md",
    "docs/CURRICULUM.md", "docs/CURRICULUM_DOMAIN_MAP.md",
    "docs/GRANULAR_CAPABILITY_MAP_PLAN.md", "docs/PROJECT_MASTER_CONTEXT.md",
]
for path in pointer_files:
    if "CURRICULUM_KNOWLEDGE_GRAPH_CONTRACT.md" not in read(path):
        raise SystemExit(f"KGC pointer missing in {path}")

print("5B FINAL POST-SYNC AUDIT: PASS")
print("Remaining 5B references are printed for manual classification:")
for path in sorted(list(Path(".").glob("*.md")) + list(Path("docs").glob("*.md"))):
    hits = [(n, line.strip()) for n, line in enumerate(read(path).splitlines(), 1) if "5B" in line]
    if hits:
        print(f"[{path}]")
        for n, line in hits:
            print(f"  L{n}: {line}")
