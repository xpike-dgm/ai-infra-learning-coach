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

# Clause delimiters prevent "7B completed; 7C active" from being interpreted as stale 7B state.
blocking_patterns = {
    "7b_not_executed": re.compile(r"7B[^,;/\n]{0,100}(henüz yürütülmedi|not executed|active-not-executed)", re.I),
    "7b_active": re.compile(r"(Aktif adım|\*\*Aktif:\*\*)[^,;\n]{0,100}7B(?:\b|\s|—|-)", re.I),
    "7b_yellow": re.compile(r"7B[^,;\n]{0,70}🟡|🟡[^,;\n]{0,70}7B", re.I),
    "7b_active_marker": re.compile(r"7B[^,;\n]{0,90}(?:—|-)[^,;\n]{0,90}\*\*AKTİF\*\*", re.I),
}

blocking: list[dict] = []
for rel in CURRENT_FILES:
    path = ROOT / rel
    if not path.is_file():
        blocking.append({"file": rel, "code": "missing_current_file"})
        continue
    text = path.read_text(encoding="utf-8")
    for code, rx in blocking_patterns.items():
        for match in rx.finditer(text):
            blocking.append({"file": rel, "code": code, "match": match.group(0)[:200]})

required = {
    "docs/TECHNICAL_ENGLISH_CEFR_PROGRESSION_SPEC.md": [
        "**Durum:** TAMAMLANDI",
        "**Final model:** `TECP-v0 — Technical English CEFR Progression`",
        "**Final decision:** `D-064`",
    ],
    "docs/EXECUTION_INDEX.md": [
        "[x] **7B — A1/A2/B1/B2+ teknik hedefleri** — `TECP-v0 / D-064`",
        "7C — Günlük English bileşeni** **AKTİF**",
    ],
    "docs/STEP_STATUS.md": ["TECP-v0 / D-064", "7C — Günlük English bileşeni", "5 A1 / 5 A2 / 5 B1"],
    "PROJECT_CONTEXT.md": ["7B ✅ TECP-v0 / D-064", "7C 🟡 Günlük English bileşeni", "Sıradaki numaralı çalışma 7C'dir"],
    "docs/HANDOFF_STATE.md": ["Son tamamlanan:** `7B — TECP-v0 / D-064`", "Aktif:** `7C — Günlük English bileşeni`"],
    "docs/START_HERE.md": ["D-064 — TECP-v0", "7B ✅ TECP-v0 / D-064", "7C 🟡 active-not-executed"],
    "docs/MASTER_PLAN.md": ["### [x] 7B — A1/A2/B1/B2+ teknik hedefleri — TECP-v0 / D-064", "### [ ] 7C — Günlük English bileşeni — **AKTİF**"],
    "docs/DECISIONS.md": ["## D-064 — Technical English CEFR Progression = TECP-v0"],
    "docs/PROGRESS_LOG.md": ["## 2026-08-27 — 7B Technical English CEFR Progression tamamlandı — TECP-v0 / D-064"],
    "vault/agent/CURRENT_CONTEXT.md": ["7B — TECP-v0 / D-064", "7C — Günlük English bileşeni"],
    "vault/agent/OPEN_LOOPS.md": ["[x] 7B CEFR + technical progression alignment", "[ ] 7C Daily English component **AKTİF**"],
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

alignment_path = ROOT / "curriculum/english/7b_cefr_progression/alignment.yaml"
alignment = yaml.safe_load(alignment_path.read_text(encoding="utf-8"))
if alignment.get("status") != "accepted_7b" or alignment.get("decision") != "D-064":
    blocking.append({"file": alignment_path.relative_to(ROOT).as_posix(), "code": "alignment_not_final", "match": str({"status": alignment.get("status"), "decision": alignment.get("decision")})})

reviews = yaml.safe_load((ROOT / "curriculum/decomposition/6c_foundations/review_queue.yaml").read_text(encoding="utf-8"))
review = next((row for row in reviews if row.get("review_id") == "review.6c.english.cefr_alignment"), None)
if not review or review.get("status") != "resolved" or review.get("resolution_owner_step") != "7B":
    blocking.append({"file": "curriculum/decomposition/6c_foundations/review_queue.yaml", "code": "cefr_review_not_resolved", "match": str(review)[:240]})

# Repo-wide candidate scan records historical/pre-step strings; only contradictions in CURRENT_FILES block.
candidate_patterns = [
    re.compile(r"7B[^\n]{0,130}(active-not-executed|henüz yürütülmedi|AKTİF|pending)", re.I),
    re.compile(r"Aktif[^\n]{0,110}7B", re.I),
    re.compile(r"7B[^\n]{0,110}(fresh PRE|kullanıcı.*onay)", re.I),
    re.compile(r"review\.6c\.english\.cefr_alignment[^\n]{0,100}(open|açık)", re.I),
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
        for match in rx.finditer(text):
            candidates.append({"file": rel, "match": match.group(0)[:230], "blocking_current_state": rel in CURRENT_FILES})

report = {
    "stage_step": "7B",
    "model": "TECP-v0",
    "decision": "D-064",
    "audit": "repo_wide_stale_reference_post_step",
    "result": "PASS" if not blocking else "FAIL",
    "blocking_findings": blocking,
    "candidate_old_or_historical_references": candidates,
    "note": "Historical 7A/7B handoff and pre-finalization references are retained as provenance. Only contradictory current living-state references block closure.",
}
out = ROOT / "curriculum/english/7b_cefr_progression/stale_reference_audit.yaml"
out.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True, width=140), encoding="utf-8")

if blocking:
    print("7B_POST_STALE_AUDIT=FAIL")
    for item in blocking:
        print("-", item)
    sys.exit(1)

print("7B_POST_STALE_AUDIT=PASS")
print(f"repo_wide_candidates={len(candidates)}")
