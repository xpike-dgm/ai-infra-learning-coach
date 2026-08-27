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
    "curriculum/decomposition/6h_research_qa/status.yaml",
]

blocking_patterns = {
    "6h_not_executed": re.compile(r"6H[^\n]{0,120}(henüz yürütülmedi|not executed|active-not-executed)", re.I),
    "6h_active": re.compile(r"(Aktif adım|\*\*Aktif:\*\*)[^\n]{0,80}6H", re.I),
    "6h_yellow": re.compile(r"6H[^\n]{0,80}🟡|🟡[^\n]{0,80}6H", re.I),
    "external_blocked": re.compile(r"BLOCKED_EXTERNAL_RESEARCH_AI_REQUIRED"),
    "old_stage_snapshot": re.compile(r"6A.?6G completed\s*/\s*6H active-not-executed", re.I),
}

blocking = []
for rel in CURRENT_FILES:
    path = ROOT / rel
    if not path.is_file():
        blocking.append({"file": rel, "code": "missing_current_file"})
        continue
    text = path.read_text(encoding="utf-8")
    for code, rx in blocking_patterns.items():
        for m in rx.finditer(text):
            blocking.append({"file": rel, "code": code, "match": m.group(0)[:180]})

# Required final state assertions.
required = {
    "docs/EXECUTION_INDEX.md": ["[x] **6H", "7A — Başlangıç ölçümü** **AKTİF**", "D-062"],
    "docs/STEP_STATUS.md": ["S6ERQA-v0 / D-062", "7A — İngilizce başlangıç ölçümü", "549 Skill", "608 Objective"],
    "PROJECT_CONTEXT.md": ["6H ✅ S6ERQA-v0 / D-062", "7A 🟡", "950 prerequisite edge"],
    "docs/HANDOFF_STATE.md": ["Son tamamlanan:** `6H — S6ERQA-v0 / D-062`", "Aktif:** `7A — İngilizce başlangıç ölçümü`"],
    "docs/START_HERE.md": ["6H ✅ S6ERQA-v0 / D-062", "7A 🟡 active-not-executed"],
    "docs/MASTER_PLAN.md": ["### [x] 6H", "S6ERQA-v0 / D-062", "### [ ] 7A — Başlangıç ölçümü — **AKTİF**"],
    "docs/DECISIONS.md": ["## D-062 — Stage 6 External Research QA = S6ERQA-v0"],
    "docs/PROGRESS_LOG.md": ["## 2026-08-27 — 6H S6ERQA-v0 / D-062 tamamlandı"],
    "vault/agent/CURRENT_CONTEXT.md": ["6H — S6ERQA-v0 / D-062", "7A — İngilizce başlangıç ölçümü"],
}
for rel, needles in required.items():
    text = (ROOT / rel).read_text(encoding="utf-8")
    for needle in needles:
        if needle not in text:
            blocking.append({"file": rel, "code": "missing_final_marker", "match": needle})

# Repo-wide candidate scan: preserve historical/spec provenance but record every old-looking reference.
candidate_patterns = [
    re.compile(r"6H[^\n]{0,100}(pending|henüz|aktif|AKTİF|zorunlu)", re.I),
    re.compile(r"external Research QA[^\n]{0,100}(pending|6H'ye)", re.I),
    re.compile(r"543 Skill[^\n]{0,100}590 Objective", re.I),
]
candidates = []
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
    "stage_step": "6H",
    "audit": "repo_wide_stale_reference_post_step",
    "result": "PASS" if not blocking else "FAIL",
    "blocking_findings": blocking,
    "candidate_old_or_historical_references": candidates,
    "note": "Candidates in historical/stable specs are not auto-rewritten; current living-state contradictions are blocking.",
}
out = ROOT / "curriculum/decomposition/6h_research_qa/stale_reference_audit.yaml"
out.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True, width=140), encoding="utf-8")

if blocking:
    print("6H_POST_STALE_AUDIT=FAIL")
    for item in blocking:
        print("-", item)
    sys.exit(1)

print("6H_POST_STALE_AUDIT=PASS")
print(f"repo_wide_candidates={len(candidates)}")
