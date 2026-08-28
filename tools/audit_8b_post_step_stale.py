from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / "ux/8b_today_home/stale_reference_audit.yaml"

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
    "AGENTS.md": ["8B: ✅ `THUX-v0 / D-069`", "Aktif adım: 8C — Günlük çalışma akışı"],
    "PROJECT_CONTEXT.md": ["8B ✅ Ana ekran — THUX-v0 / D-069", "8C 🟡 Günlük çalışma akışı — AKTİF"],
    "docs/START_HERE.md": ["8B ✅ THUX-v0 / D-069", "8C — Günlük çalışma akışı"],
    "docs/HANDOFF_STATE.md": ["8B ✅ THUX-v0 / D-069", "Aktif:** `8C — Günlük çalışma akışı"],
    "docs/STEP_STATUS.md": ["8B — Ana ekran** | ✅", "8C — Günlük çalışma akışı** | 🟡 Aktif"],
    "docs/EXECUTION_INDEX.md": ["[x] **8B — Ana ekran** — `THUX-v0 / D-069`", "**8C — Günlük çalışma akışı** **AKTİF**"],
    "docs/MASTER_PLAN.md": ["[x] 8B — Ana ekran — THUX-v0 / D-069", "8C — Günlük çalışma akışı — **AKTİF**"],
    "docs/LOCAL_MANAGER_HANDOFF.md": ["AŞAMA 8B ✅ THUX-v0 / D-069", "Aktif adım:** `8C — Günlük çalışma akışı"],
    "vault/agent/CURRENT_CONTEXT.md": ["THUX-v0 / D-069", "Aktif adım **8C — Günlük çalışma akışı**"],
    "vault/agent/OPEN_LOOPS.md": ["[x] 8B Ana ekran", "[ ] 8C Günlük çalışma akışı **AKTİF**"],
}

STALE_PATTERNS = [
    re.compile(r"\b8B\b(?:(?![;|\n]).){0,140}(?:🟡|active-not-executed|AKTİF[^;|\n]{0,50}(?:HENÜZ|henüz)|henüz yürütülmedi|HENÜZ YÜRÜTÜLMEDİ)", re.I),
    re.compile(r"(?:Aktif adım|Aktif step|Sıradaki numaralı çalışma|Sıradaki gerçek numbered work)[^\n]{0,120}\b8B\b", re.I),
    re.compile(r"\*\*(?:Son tamamlanan|Son tamamlanan numaralı adım):\*\*[^\n]{0,100}\b8A\b", re.I),
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
            stale.append({"pattern": pattern.pattern, "match": m.group(0)[:200]})
    missing = [m for m in REQUIRED[rel] if m not in text]
    if stale or missing:
        blocking.append({"file": rel, "stale_matches": stale, "missing_markers": missing})
    checked.append({"file": rel, "stale_matches": len(stale), "missing_markers": missing})

# Accepted 8B artifact / QA guards.
home_path = ROOT / "ux/8b_today_home/home.yaml"
qa_path = ROOT / "ux/8b_today_home/qa_report.yaml"
spec_path = ROOT / "docs/TODAY_HOME_SCREEN_SPEC.md"
if not home_path.exists() or not qa_path.exists() or not spec_path.exists():
    blocking.append({"file": "8B accepted artifacts", "kind": "accepted_artifact_missing"})
else:
    home = yaml.safe_load(home_path.read_text(encoding="utf-8"))
    qa = yaml.safe_load(qa_path.read_text(encoding="utf-8"))
    spec = spec_path.read_text(encoding="utf-8")
    if home.get("status") != "accepted_8b" or home.get("decision") != "D-069":
        blocking.append({"file": str(home_path.relative_to(ROOT)), "kind": "not_accepted", "status": home.get("status"), "decision": home.get("decision")})
    if qa.get("result") != "PASS" or qa.get("checks_failed") != 0 or qa.get("checks_passed") != 90:
        blocking.append({"file": str(qa_path.relative_to(ROOT)), "kind": "qa_not_pass", "result": qa.get("result"), "passed": qa.get("checks_passed"), "failed": qa.get("checks_failed")})
    if "**Status:** ACCEPTED — independent 8B QA PASS" not in spec or "**Decision:** `D-069`" not in spec:
        blocking.append({"file": str(spec_path.relative_to(ROOT)), "kind": "spec_not_accepted"})

# Durable decision must exist.
decisions = (ROOT / "docs/DECISIONS.md").read_text(encoding="utf-8")
if "## D-069 — Today Home UX = THUX-v0" not in decisions:
    blocking.append({"file": "docs/DECISIONS.md", "kind": "D-069_missing"})

# Repo-wide inventory: stale-like 8B wording outside living sources is reported but non-blocking
# when it is preserved in historical logs/spec candidates/tooling. Current sources above are strict.
historical_hits: list[dict] = []
current_set = {str((ROOT / x).resolve()) for x in CURRENT}
repo_files_scanned = 0
for path in ROOT.rglob("*"):
    if not path.is_file() or ".git" in path.parts:
        continue
    repo_files_scanned += 1
    if path.suffix not in HISTORICAL_SUFFIXES or str(path.resolve()) in current_set:
        continue
    try:
        text = path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        continue
    samples = []
    for pattern in STALE_PATTERNS[:2]:
        m = pattern.search(text)
        if m:
            samples.append(m.group(0)[:180])
    if samples:
        historical_hits.append({
            "file": str(path.relative_to(ROOT)),
            "sample": samples[0],
            "classification": "historical_non_blocking",
        })

report = {
    "stage_step": "8B",
    "model": "THUX-v0",
    "decision": "D-069",
    "audit": "repo_wide_stale_reference_post_step",
    "result": "PASS" if not blocking else "FAIL",
    "repo_files_scanned": repo_files_scanned,
    "checked_current_files": checked,
    "historical_non_blocking_hits": historical_hits[:40],
    "blocking_findings": blocking,
    "historical_scope_policy": "Historical specs/research/logs/tools may preserve at-the-time 8B future/candidate language; current/living sources must agree on 8B complete and 8C active-not-executed.",
}
REPORT.parent.mkdir(parents=True, exist_ok=True)
REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"8B_POST_STALE_AUDIT={report['result']}")
print(f"repo_files_scanned={repo_files_scanned} current_checked={len(checked)} blocking={len(blocking)} historical_non_blocking={len(historical_hits)}")
for finding in blocking:
    print("-", finding)

sys.exit(0 if not blocking else 1)
