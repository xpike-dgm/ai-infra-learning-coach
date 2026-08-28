from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / "ux/8a_information_architecture/stale_reference_audit.yaml"

CURRENT = [
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

REQUIRED = {
    "AGENTS.md": ["8A: ✅ `UXIA-v0 / D-068`", "Aktif adım: 8B — Ana ekran"],
    "PROJECT_CONTEXT.md": ["8A ✅ Bilgi mimarisi — UXIA-v0 / D-068", "8B 🟡 Ana ekran — AKTİF"],
    "docs/START_HERE.md": ["8A ✅ UXIA-v0 / D-068", "8B — Ana ekran"],
    "docs/HANDOFF_STATE.md": ["8A ✅ UXIA-v0 / D-068", "Aktif:** `8B — Ana ekran"],
    "docs/STEP_STATUS.md": ["8A — Bilgi mimarisi** | ✅", "8B — Ana ekran** | 🟡 Aktif"],
    "docs/EXECUTION_INDEX.md": ["[x] **8A — Bilgi mimarisi** — `UXIA-v0 / D-068`", "**8B — Ana ekran** **AKTİF**"],
    "docs/MASTER_PLAN.md": ["[x] 8A — Bilgi mimarisi — UXIA-v0 / D-068", "8B — Ana ekran — **AKTİF**"],
    "docs/LOCAL_MANAGER_HANDOFF.md": ["8A completion addendum — D-068", "8B — Ana ekran"],
    "vault/agent/CURRENT_CONTEXT.md": ["UXIA-v0 / D-068", "Aktif adım **8B — Ana ekran**"],
    "vault/agent/OPEN_LOOPS.md": ["[x] 8A Bilgi mimarisi", "[ ] 8B Ana ekran **AKTİF**"],
}

STALE_PATTERNS = [
    re.compile(r"\b8A\b(?:(?![;|\n]).){0,120}(?:🟡|active-not-executed|AKTİF[^;|\n]{0,40}(?:HENÜZ|henüz)|henüz yürütülmedi|HENÜZ YÜRÜTÜLMEDİ)", re.I),
    re.compile(r"(?:Aktif adım|Aktif step|Sıradaki numaralı çalışma|Sıradaki gerçek numbered work)[^\n]{0,100}\b8A\b", re.I),
    re.compile(r"\*\*Son tamamlanan:\*\*[^\n]{0,80}\b7E\b", re.I),
]

HISTORICAL_SUFFIXES = {".md", ".yaml", ".yml", ".py"}
blocking: list[dict] = []
checked: list[dict] = []

for rel in CURRENT:
    path = ROOT / rel
    if not path.exists():
        blocking.append({"file": rel, "kind": "missing_current_file"})
        continue
    text = path.read_text(encoding="utf-8")
    stale = []
    for pattern in STALE_PATTERNS:
        for m in pattern.finditer(text):
            stale.append({"pattern": pattern.pattern, "match": m.group(0)[:180]})
    missing = [m for m in REQUIRED[rel] if m not in text]
    if stale or missing:
        blocking.append({"file": rel, "stale_matches": stale, "missing_markers": missing})
    checked.append({"file": rel, "stale_matches": len(stale), "missing_markers": missing})

# Accepted artifact guards.
ia_path = ROOT / "ux/8a_information_architecture/ia.yaml"
qa_path = ROOT / "ux/8a_information_architecture/qa_report.yaml"
if not ia_path.exists() or not qa_path.exists():
    blocking.append({"file": "ux/8a_information_architecture", "kind": "accepted_artifact_missing"})
else:
    ia = yaml.safe_load(ia_path.read_text(encoding="utf-8"))
    qa = yaml.safe_load(qa_path.read_text(encoding="utf-8"))
    if ia.get("status") != "accepted_8a" or ia.get("decision") != "D-068":
        blocking.append({"file": str(ia_path.relative_to(ROOT)), "kind": "not_accepted", "status": ia.get("status"), "decision": ia.get("decision")})
    if qa.get("result") != "PASS" or qa.get("counts", {}).get("failures") != 0:
        blocking.append({"file": str(qa_path.relative_to(ROOT)), "kind": "qa_not_pass", "result": qa.get("result")})

# Repo-wide historical inventory: matches outside current sources are recorded but do not
# block when they preserve at-the-time candidate/future-state language in specs/logs/tools.
historical_hits: list[dict] = []
current_set = {str((ROOT / x).resolve()) for x in CURRENT}
for path in ROOT.rglob("*"):
    if not path.is_file() or ".git" in path.parts or path.suffix not in HISTORICAL_SUFFIXES:
        continue
    if str(path.resolve()) in current_set:
        continue
    try:
        text = path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        continue
    samples = []
    for pattern in STALE_PATTERNS[:2]:
        m = pattern.search(text)
        if m:
            samples.append(m.group(0)[:160])
    if samples:
        historical_hits.append({
            "file": str(path.relative_to(ROOT)),
            "sample": samples[0],
            "classification": "historical_non_blocking",
        })

report = {
    "stage_step": "8A",
    "model": "UXIA-v0",
    "decision": "D-068",
    "audit": "repo_wide_stale_reference_post_step",
    "result": "PASS" if not blocking else "FAIL",
    "repo_files_scanned": sum(1 for p in ROOT.rglob("*") if p.is_file() and ".git" not in p.parts),
    "checked_current_files": checked,
    "historical_non_blocking_hits": historical_hits[:30],
    "blocking_findings": blocking,
    "historical_scope_policy": "Historical specs/research/logs/tools may preserve at-the-time 8A future-state language; current/living sources must agree on 8A complete and 8B active-not-executed.",
}
REPORT.parent.mkdir(parents=True, exist_ok=True)
REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"8A_POST_STALE_AUDIT={report['result']}")
print(f"repo_files_scanned={report['repo_files_scanned']} current_checked={len(checked)} blocking={len(blocking)} historical_non_blocking={len(historical_hits)}")
for finding in blocking:
    print("-", finding)

sys.exit(0 if not blocking else 1)
