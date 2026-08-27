from __future__ import annotations

from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]


def read(rel: str) -> str:
    return (ROOT / rel).read_text(encoding="utf-8")


def write(rel: str, text: str) -> None:
    (ROOT / rel).write_text(text, encoding="utf-8")


# START_HERE — normalize the Stage 8 current map if an older mirror survived.
rel = "docs/START_HERE.md"
text = read(rel)
text = text.replace(
    "- 8 UX — **8A 🟡 active-not-executed**",
    "- 8 UX — **8A ✅ UXIA-v0 / D-068; 8B 🟡 active-not-executed**",
)
text = text.replace(
    "- AŞAMA 8: **8A 🟡 active-not-executed**",
    "- AŞAMA 8: **8A ✅ UXIA-v0 / D-068; 8B 🟡 active-not-executed**",
)
# Keep completed and active markers on separate lines where possible to avoid semantic ambiguity.
text = text.replace(
    "- AŞAMA 8: **8A ✅ UXIA-v0 / D-068; 8B 🟡 active-not-executed**",
    "- AŞAMA 8A ✅ **UXIA-v0 / D-068**\n- AŞAMA 8B 🟡 **active-not-executed**",
)
text = text.replace(
    "- 8 UX — **8A ✅ UXIA-v0 / D-068; 8B 🟡 active-not-executed**",
    "- 8 UX — **8A ✅ UXIA-v0 / D-068**\n  - **8B 🟡 active-not-executed**",
)
write(rel, text)


# EXECUTION_INDEX — replace the lower current-position mirror, not historical step records.
rel = "docs/EXECUTION_INDEX.md"
text = read(rel)
text = re.sub(
    r"# Güncel Konum\n\n\*\*Tamamlanan:\*\* `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A–7E`  \n\*\*Son tamamlanan:\*\* \*\*`7E — TEPM-v0 / D-067`\*\*  \n\*\*Aktif:\*\* \*\*`8A — Bilgi mimarisi`\*\* — active-not-executed\n\nAŞAMA 7 tamamen tamamlandı: EED-v0 → TECP-v0 → DECP-v0 → TEIP-v0 → TEPM-v0\. English mastery truth exact GRE/RVR-backed Skill/Objective state'tir; CEFR yalnız qualified Technical English profile metadata/summary'dir\.\n\n8A başlamadan fresh PRE-STEP GitHub refresh \+ kullanıcı açık onayı zorunludur\.",
    """# Güncel Konum

**Tamamlanan:** `1A–1D`, `2A–2F`, `3A–3H`, `4A–4E`, `5A–5D`, `6A–6H`, `7A–7E`, `8A`  
**Son tamamlanan:** **`8A — UXIA-v0 / D-068`**  
**Aktif:** **`8B — Ana ekran`** — active-not-executed

8A `UXIA-v0` ile Today/Learn/Progress/Profile semantic shell'i, shared detail surfaces ve focused task/assessment flows kilitlendi; UI hierarchy canonical mastery/prerequisite truth yerine geçmez.

8B başlamadan fresh PRE-STEP GitHub refresh + kullanıcı açık onayı zorunludur.""",
    text,
    count=1,
)
# Fallback for minor formatting changes after future finalizer edits.
text = re.sub(
    r"(?m)^\*\*Son tamamlanan:\*\* \*\*`7E — TEPM-v0 / D-067`\*\*\s*$",
    "**Son tamamlanan:** **`8A — UXIA-v0 / D-068`**  ",
    text,
)
text = re.sub(
    r"(?m)^\*\*Aktif:\*\* \*\*`8A — Bilgi mimarisi`\*\* — active-not-executed\s*$",
    "**Aktif:** **`8B — Ana ekran`** — active-not-executed",
    text,
)
text = text.replace(
    "8A başlamadan fresh PRE-STEP GitHub refresh + kullanıcı açık onayı zorunludur.",
    "8B başlamadan fresh PRE-STEP GitHub refresh + kullanıcı açık onayı zorunludur.",
)
write(rel, text)


# LOCAL_MANAGER_HANDOFF — this is a living takeover document, so all explicit current-state
# mirrors must advance to 8A complete / 8B active. Historical model descriptions remain intact.
rel = "docs/LOCAL_MANAGER_HANDOFF.md"
text = read(rel)
text = text.replace(
    "8. Current execution state'in `AŞAMA 6 ✅ / AŞAMA 7 ✅ / 8A active-not-executed` olduğunu living-memory setiyle doğrula.",
    "8. Current execution state'in `AŞAMA 6 ✅ / AŞAMA 7 ✅ / 8A ✅ UXIA-v0 / D-068 / 8B active-not-executed` olduğunu living-memory setiyle doğrula.",
)
text = text.replace("- AŞAMA 8A 🟡 active-not-executed\n- 8B–20 ⬜", "- AŞAMA 8A ✅ UXIA-v0 / D-068\n- AŞAMA 8B 🟡 active-not-executed\n- 8C–20 ⬜")
text = re.sub(
    r"# 22\. Current exact state — en kritik takeover bilgisi\n\n\*\*Son tamamlanan numaralı adım:\*\* `7E — English mastery`  \n\*\*Final:\*\* `TEPM-v0 — Technical English Mastery Profile` / D-067  \n\*\*Canonical:\*\* `docs/TECHNICAL_ENGLISH_MASTERY_PROFILE_SPEC\.md` \+ `curriculum/english/7e_mastery_profile/`\n\n\*\*AŞAMA 7:\*\* ✅ TAMAMLANDI  \n\*\*Aktif adım:\*\* `8A — Bilgi mimarisi`  \n\*\*Durum:\*\* \*\*HENÜZ YÜRÜTÜLMEDİ\*\*\n\n7E exact GRE/RVR-backed English Skill state'ini learner-facing derived profile'a dönüştürür; CEFR qualified Technical English summary'dir, general-English certification değildir\. B2\+ aggregate completion değildir\.\n\n8A için:\n\n```text\nfresh 8A PRE-STEP GitHub refresh\n→ user explicit approval verification\n→ 8A execution\n→ independent QA\n→ D-050 POST sync\n→ repo-wide stale-reference audit\n```",
    """# 22. Current exact state — en kritik takeover bilgisi

**Son tamamlanan numaralı adım:** `8A — Bilgi mimarisi`  
**Final:** `UXIA-v0 — Adaptive Learning Information Architecture` / D-068  
**Canonical:** `docs/INFORMATION_ARCHITECTURE_SPEC.md` + `ux/8a_information_architecture/`

**AŞAMA 7:** ✅ TAMAMLANDI  
**AŞAMA 8A:** ✅ TAMAMLANDI  
**Aktif adım:** `8B — Ana ekran`  
**Durum:** **HENÜZ YÜRÜTÜLMEDİ**

8A exactly four semantic primary destination'ı kilitledi: Today / Learn / Progress / Profile. Assessment, English, AI Tutor ve remediation/retention top-level silo değildir; canonical mastery/prerequisite/planner truth ownership korunur.

8B için:

```text
fresh 8B PRE-STEP GitHub refresh
→ user explicit approval verification
→ 8B execution
→ independent QA
→ D-050 POST sync
→ repo-wide stale-reference audit
```""",
    text,
    count=1,
)
# Generic explicit current-state cleanup for surviving takeover/addendum clauses.
text = re.sub(r"(?m)^- AŞAMA 8A 🟡(?: active-not-executed)?\s*$", "- AŞAMA 8A ✅ UXIA-v0 / D-068", text)
text = re.sub(r"(?m)^\*\*Aktif adım:\*\* `8A — Bilgi mimarisi`\s*$", "**Aktif adım:** `8B — Ana ekran`  ", text)
text = re.sub(r"(?i)\b8A henüz yürütülmedi\b", "8B henüz yürütülmedi", text)
text = re.sub(r"(?i)(Sıradaki gerçek numbered work:\*\*\s*`)8A\b[^`]*`", r"\g<1>8B — Ana ekran`", text)
write(rel, text)


# Assertions: living files must state 8A complete and 8B active without stale current clauses.
required = {
    "docs/START_HERE.md": ["8A ✅ UXIA-v0 / D-068", "8B 🟡 active-not-executed"],
    "docs/EXECUTION_INDEX.md": ["**Son tamamlanan:** **`8A — UXIA-v0 / D-068`**", "**Aktif:** **`8B — Ana ekran`** — active-not-executed"],
    "docs/LOCAL_MANAGER_HANDOFF.md": ["AŞAMA 8A ✅ UXIA-v0 / D-068", "**Aktif adım:** `8B — Ana ekran`", "fresh 8B PRE-STEP GitHub refresh"],
}
for rel, markers in required.items():
    data = read(rel)
    missing = [m for m in markers if m not in data]
    if missing:
        raise RuntimeError(f"{rel}: required post-8A markers missing: {missing}")

for rel in required:
    data = read(rel)
    forbidden_patterns = [
        r"\b8A\b(?:(?![;|\n]).){0,120}(?:🟡|active-not-executed|AKTİF[^;|\n]{0,40}(?:HENÜZ|henüz)|henüz yürütülmedi|HENÜZ YÜRÜTÜLMEDİ)",
        r"(?:Aktif adım|Aktif step|Sıradaki numaralı çalışma|Sıradaki gerçek numbered work)[^\n]{0,100}\b8A\b",
    ]
    hits = []
    for raw in forbidden_patterns:
        m = re.search(raw, data, re.I)
        if m:
            hits.append(m.group(0))
    if hits:
        raise RuntimeError(f"{rel}: stale 8A current-state patterns remain: {hits}")

print("8A_LIVING_STATE_FIX=PASS")
