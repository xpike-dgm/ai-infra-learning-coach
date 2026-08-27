from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
CURRENT_FILES = [
    "AGENTS.md",
    "PROJECT_CONTEXT.md",
    "docs/START_HERE.md",
    "docs/HANDOFF_STATE.md",
    "docs/STEP_STATUS.md",
    "docs/EXECUTION_INDEX.md",
    "docs/MASTER_PLAN.md",
    "docs/LOCAL_MANAGER_HANDOFF.md",
    "vault/agent/CURRENT_CONTEXT.md",
    "vault/agent/OPEN_LOOPS.md",
]

# Delimiters matter: a line such as "7A completed; 7B active-not-executed" or
# "7A ✅, 7B 🟡" must not be interpreted as saying that 7A is still active.
# These expressions only inspect the clause that belongs to 7A.
blocking_patterns = {
    "7a_not_executed": re.compile(r"7A[^;,/\n]{0,90}(henüz yürütülmedi|not executed|active-not-executed)", re.I),
    "7a_active": re.compile(r"(Aktif adım|\*\*Aktif:\*\*)[^;,\n]{0,90}7A(?:\b|\s|—|-)", re.I),
    "7a_yellow": re.compile(r"7A[^;,\n]{0,60}🟡|🟡[^;,\n]{0,60}7A", re.I),
    "7a_stage_active_marker": re.compile(r"7A[^;,\n]{0,80}(?:—|-)[^;,\n]{0,80}\*\*AKTİF\*\*", re.I),
}

blocking: list[dict] = []
for rel in CURRENT_FILES:
    path = ROOT / rel
    if not path.is_file():
        blocking.append({"file": rel, "code": "missing_current_file"})
        continue
    text = path.read_text(encoding="utf-8")
    for code, rx in blocking_patterns.items():
        for m in rx.finditer(text):
            blocking.append({"file": rel, "code": code, "match": m.group(0)[:180]})

required = {
    "docs/ENGLISH_ENTRY_DIAGNOSTIC_SPEC.md": ["**Durum:** TAMAMLANDI", "**Final model:** `EED-v0 — English Entry Diagnostic`", "**Final decision:** `D-063`"],
    "docs/EXECUTION_INDEX.md": ["[x] **7A — Başlangıç ölçümü** — `EED-v0 / D-063`", "7B — A1/A2/B1/B2+ teknik hedefleri** **AKTİF**"],
    "docs/STEP_STATUS.md": ["EED-v0 / D-063", "7B — A1/A2/B1/B2+ teknik hedefleri", "15 Skill / 15 Objective / 16 hard edge"],
    "PROJECT_CONTEXT.md": ["7A ✅ EED-v0 / D-063", "7B 🟡 A1/A2/B1/B2+ teknik hedefleri", "Sıradaki numaralı çalışma 7B'dir"],
    "docs/HANDOFF_STATE.md": ["Son tamamlanan:** `7A — EED-v0 / D-063`", "Aktif:** `7B — A1/A2/B1/B2+ teknik hedefleri`"],
    "docs/START_HERE.md": ["D-063 — EED-v0", "7A ✅ EED-v0 / D-063", "7B 🟡 active-not-executed"],
    "docs/MASTER_PLAN.md": ["### [x] 7A — Başlangıç ölçümü — EED-v0 / D-063", "### [ ] 7B — A1/A2/B1/B2+ teknik hedefleri — **AKTİF**"],
    "docs/DECISIONS.md": ["## D-063 — English Entry Diagnostic = EED-v0"],
    "docs/PROGRESS_LOG.md": ["## 2026-08-27 — 7A English Entry Diagnostic tamamlandı — EED-v0 / D-063"],
    "vault/agent/CURRENT_CONTEXT.md": ["7A — EED-v0 / D-063", "7B — A1/A2/B1/B2+ teknik hedefleri"],
    "vault/agent/OPEN_LOOPS.md": ["[x] 7A English entry diagnostic", "[ ] 7B CEFR + technical progression alignment **AKTİF**"],
}
for rel, needles in required.items():
    path = ROOT / rel
    if not path.is_file():
        blocking.append({"file": rel, "code": "missing_required_file"})
        continue
    text = path.read_text(encoding="utf-8")
    for needle in needles:
        if needle not in text:
            blocking.append({"file": rel, "code": "missing_final_marker", "match": needle})

blueprint = yaml.safe_load((ROOT / "curriculum/english/7a_entry_diagnostic/blueprint.yaml").read_text(encoding="utf-8"))
if blueprint.get("status") != "accepted_7a" or blueprint.get("decision") != "D-063":
    blocking.append({"file": "curriculum/english/7a_entry_diagnostic/blueprint.yaml", "code": "blueprint_not_final", "match": str({"status": blueprint.get("status"), "decision": blueprint.get("decision")})})
if blueprint.get("cefr_alignment_status") != "pending_7B" or blueprint.get("cefr_level") is not None:
    blocking.append({"file": "curriculum/english/7a_entry_diagnostic/blueprint.yaml", "code": "premature_cefr_claim", "match": str({"cefr_alignment_status": blueprint.get("cefr_alignment_status"), "cefr_level": blueprint.get("cefr_level")})})

# Repo-wide candidate scan records old-looking references but only CURRENT_FILES
# contradictions above block closure.
candidate_patterns = [
    re.compile(r"7A[^\n]{0,120}(active-not-executed|henüz yürütülmedi|AKTİF|pending)", re.I),
    re.compile(r"Aktif[^\n]{0,100}7A", re.I),
    re.compile(r"7A[^\n]{0,100}(fresh PRE|kullanıcı.*onay)", re.I),
]
candidates: list[dict] = []
for path in ROOT.rglob("*"):
    if not path.is_file() or ".git" in path.parts or path.suffix.lower() not in {".md", ".yaml", ".yml", ".py"}:
        continue
    try:
        text = path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        continue
    rel = path.relative_to(ROOT).as_posix()
    for rx in candidate_patterns:
        for m in rx.finditer(text):
            candidates.append({"file": rel, "match": m.group(0)[:220], "blocking_current_state": rel in CURRENT_FILES})

report = {
    "stage_step": "7A",
    "model": "EED-v0",
    "decision": "D-063",
    "audit": "repo_wide_stale_reference_post_step",
    "result": "PASS" if not blocking else "FAIL",
    "blocking_findings": blocking,
    "candidate_old_or_historical_references": candidates,
    "note": "Historical/pre-step/progress references are recorded, not auto-rewritten. Contradictory current living-state references are blocking.",
}
out = ROOT / "curriculum/english/7a_entry_diagnostic/stale_reference_audit.yaml"
out.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True, width=140), encoding="utf-8")

if blocking:
    print("7A_POST_STALE_AUDIT=FAIL")
    for item in blocking:
        print("-", item)
    sys.exit(1)

print("7A_POST_STALE_AUDIT=PASS")
print(f"repo_wide_candidates={len(candidates)}")
