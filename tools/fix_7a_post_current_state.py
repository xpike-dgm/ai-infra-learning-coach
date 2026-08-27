from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

# Vault current context: use the exact pre-7A sentence; the earlier broad regex intentionally
# did not match because this snapshot says "AŞAMA 6 tamamen tamamlandı" rather than "6A–6H tamamlandı".
p = ROOT / "vault/agent/CURRENT_CONTEXT.md"
t = p.read_text(encoding="utf-8")
old = "AŞAMA 6 tamamen tamamlandı. Son tamamlanan adım **6H — S6ERQA-v0 / D-062**. Final Stage 6: 549 Skill / 608 Objective / 950 edge; hard DAG 549/549; 10/10 6H review resolved. Aktif adım **7A — İngilizce başlangıç ölçümü**; henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur."
new = "AŞAMA 6 tamamen tamamlandı. 7A da tamamlandı. Son tamamlanan adım **7A — EED-v0 / D-063**. EED-v0 final diagnostic scope: 15 D01 English Skill / 15 Objective / 16 English hard edge / 15 task family. Aktif adım **7B — A1/A2/B1/B2+ teknik hedefleri**; henüz yürütülmedi. Fresh PRE-STEP + kullanıcı açık onayı zorunludur."
if old in t:
    t = t.replace(old, new, 1)
elif new not in t:
    raise RuntimeError("CURRENT_CONTEXT exact execution sentence not found")
p.write_text(t, encoding="utf-8")

# LOCAL_MANAGER_HANDOFF contains a second prose current-state mirror using punctuation that
# is different from the bootstrap line handled by the main finalizer.
p = ROOT / "docs/LOCAL_MANAGER_HANDOFF.md"
t = p.read_text(encoding="utf-8")
t = t.replace(
    "**7A — İngilizce başlangıç ölçümü**, active-not-executed",
    "**7A — İngilizce başlangıç ölçümü**, completed as `EED-v0 / D-063`; **7B — A1/A2/B1/B2+ teknik hedefleri**, active-not-executed",
)
t = t.replace(
    "7A — İngilizce başlangıç ölçümü active-not-executed",
    "7A — İngilizce başlangıç ölçümü completed as EED-v0 / D-063; 7B — A1/A2/B1/B2+ teknik hedefleri active-not-executed",
)
t = t.replace(
    "7A — İngilizce başlangıç ölçümü — active-not-executed",
    "7A — İngilizce başlangıç ölçümü — completed as EED-v0 / D-063; 7B — A1/A2/B1/B2+ teknik hedefleri — active-not-executed",
)
p.write_text(t, encoding="utf-8")

print("7A_POST_CURRENT_STATE_FIX=PASS")
