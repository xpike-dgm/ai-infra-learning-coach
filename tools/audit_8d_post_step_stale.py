from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / "ux/8d_assessment_session/stale_reference_audit.yaml"

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
    "AGENTS.md": ["8D: ✅ `ASUX-v0 / D-071`", "Aktif adım: 8E — Skill/progress/weakness UX"],
    "PROJECT_CONTEXT.md": ["8D ✅ Sınav UX — ASUX-v0 / D-071", "8E \U0001f7e1 Skill/progress/weakness UX — AKTİF"],
    "docs/START_HERE.md": ["8D ✅ ASUX-v0 / D-071", "8E — Skill/progress/weakness UX"],
    "docs/HANDOFF_STATE.md": ["8D ✅ ASUX-v0 / D-071", "Aktif:** `8E — Skill/progress/weakness UX"],
    "docs/STEP_STATUS.md": ["8D — Sınav UX** | ✅", "8E — Skill/progress/weakness UX** | \U0001f7e1 Aktif"],
    "docs/EXECUTION_INDEX.md": ["[x] **8D — Sınav UX** — `ASUX-v0 / D-071`", "**8E — Skill/progress/weakness UX** **AKTİF**"],
    "docs/MASTER_PLAN.md": ["[x] 8D — Sınav UX — ASUX-v0 / D-071", "8E — Skill/progress/weakness UX — **AKTİF**"],
    "docs/LOCAL_MANAGER_HANDOFF.md": ["AŞAMA 8D ✅ ASUX-v0 / D-071", "Aktif adım:** `8E — Skill/progress/weakness UX"],
    "vault/agent/CURRENT_CONTEXT.md": ["ASUX-v0 / D-071", "Aktif adım **8E — Skill/progress/weakness UX**"],
    "vault/agent/OPEN_LOOPS.md": ["[x] 8D Sınav UX", "[ ] 8E Skill/progress/weakness UX **AKTİF**"],
}

STALE_PATTERNS = [
    re.compile(r"\b8D\b(?:(?![;|\n]).){0,140}(?:\U0001f7e1|active-not-executed|AKTİF[^;|\n]{0,50}(?:HENÜZ|henüz)|henüz yürütülmedi|HENÜZ YÜRÜTÜLMEDİ)", re.I),
    re.compile(r"(?:Aktif adım|Aktif step|Sıradaki numaralı çalışma|Sıradaki gerçek numbered work)[^\n]{0,120}\b8D\b", re.I),
    re.compile(r"\*\*(?:Son tamamlanan|Son tamamlanan numaralı adım):\*\*[^\n]{0,100}\b8C\b", re.I),
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

# Accepted 8D artifact / QA guards.
session_path = ROOT / "ux/8d_assessment_session/session.yaml"
qa_path = ROOT / "ux/8d_assessment_session/qa_report.yaml"
spec_path = ROOT / "docs/ASSESSMENT_SESSION_UX_SPEC.md"
if not session_path.exists() or not qa_path.exists() or not spec_path.exists():
    blocking.append({"file": "8D accepted artifacts", "kind": "accepted_artifact_missing"})
else:
    session = yaml.safe_load(session_path.read_text(encoding="utf-8"))
    qa = yaml.safe_load(qa_path.read_text(encoding="utf-8"))
    spec = spec_path.read_text(encoding="utf-8")
    if session.get("status") != "accepted_8d" or session.get("decision") != "D-071":
        blocking.append({"file": str(session_path.relative_to(ROOT)), "kind": "not_accepted",
                         "status": session.get("status"), "decision": session.get("decision")})
    if qa.get("result") != "PASS" or qa.get("checks_failed") != 0 or qa.get("checks_passed") != 107:
        blocking.append({"file": str(qa_path.relative_to(ROOT)), "kind": "qa_not_pass", "result": qa.get("result"),
                         "passed": qa.get("checks_passed"), "failed": qa.get("checks_failed")})
    if "**Status:** ACCEPTED — independent 8D QA PASS" not in spec or "**Decision:** `D-071`" not in spec:
        blocking.append({"file": str(spec_path.relative_to(ROOT)), "kind": "spec_not_accepted"})

# Durable decision must exist.
decisions = (ROOT / "docs/DECISIONS.md").read_text(encoding="utf-8")
if "## D-071 — Assessment Session UX = ASUX-v0" not in decisions:
    blocking.append({"file": "docs/DECISIONS.md", "kind": "D-071_missing"})

# Prior accepted UX steps must not have been silently reopened.
for rel, status_key, status_val, decision_val in [
    ("ux/8b_today_home/home.yaml", "status", "accepted_8b", "D-069"),
    ("ux/8c_daily_working_flow/flow.yaml", "status", "accepted_8c", "D-070"),
]:
    path = ROOT / rel
    if path.exists():
        data = yaml.safe_load(path.read_text(encoding="utf-8"))
        if data.get(status_key) != status_val or data.get("decision") != decision_val:
            blocking.append({"file": rel, "kind": "prior_step_regressed",
                             "status": data.get(status_key), "decision": data.get("decision")})

# Repo-wide inventory: stale-like 8D wording outside living sources is reported but non-blocking
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
    "stage_step": "8D",
    "model": "ASUX-v0",
    "decision": "D-071",
    "audit": "repo_wide_stale_reference_post_step",
    "result": "PASS" if not blocking else "FAIL",
    "repo_files_scanned": repo_files_scanned,
    "checked_current_files": checked,
    "historical_non_blocking_hits": historical_hits[:40],
    "blocking_findings": blocking,
    "historical_scope_policy": "Historical specs/research/logs/tools may preserve at-the-time 8D future/candidate language; current/living sources must agree on 8D complete and 8E active-not-executed.",
}
REPORT.parent.mkdir(parents=True, exist_ok=True)
REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"8D_POST_STALE_AUDIT={report['result']}")
print(f"repo_files_scanned={repo_files_scanned} current_checked={len(checked)} "
      f"blocking={len(blocking)} historical_non_blocking={len(historical_hits)}")
for finding in blocking:
    print("-", str(finding).encode("ascii", "backslashreplace").decode("ascii"))

sys.exit(0 if not blocking else 1)
