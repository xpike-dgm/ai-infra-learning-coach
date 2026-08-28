from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / "ux/8f_design_system/stale_reference_audit.yaml"

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
    "AGENTS.md": ["8F: ✅ `VDSX-v0 / D-073`", "Aktif adım: 8G — Wireframe/prototip"],
    "PROJECT_CONTEXT.md": ["8F ✅ Tasarım sistemi — VDSX-v0 / D-073", "8G \U0001f7e1 Wireframe/prototip — AKTİF"],
    "docs/START_HERE.md": ["8F ✅ VDSX-v0 / D-073", "8G — Wireframe/prototip"],
    "docs/HANDOFF_STATE.md": ["8F ✅ VDSX-v0 / D-073", "Aktif:** `8G — Wireframe/prototip"],
    "docs/STEP_STATUS.md": ["8F — Tasarım sistemi** | ✅", "8G — Wireframe/prototip** | \U0001f7e1 Aktif"],
    "docs/EXECUTION_INDEX.md": ["[x] **8F — Tasarım sistemi** — `VDSX-v0 / D-073`", "**8G — Wireframe/prototip** **AKTİF**"],
    "docs/MASTER_PLAN.md": ["[x] 8F — Tasarım sistemi — VDSX-v0 / D-073", "8G — Wireframe/prototip — **AKTİF**"],
    "docs/LOCAL_MANAGER_HANDOFF.md": ["AŞAMA 8F ✅ VDSX-v0 / D-073", "Aktif adım:** `8G — Wireframe/prototip"],
    "vault/agent/CURRENT_CONTEXT.md": ["VDSX-v0 / D-073", "Aktif adım **8G — Wireframe/prototip**"],
    "vault/agent/OPEN_LOOPS.md": ["[x] 8F Tasarım sistemi", "[ ] 8G Wireframe/prototip **AKTİF**"],
}

STALE_PATTERNS = [
    re.compile(r"\b8F\b(?:(?![;|\n]).){0,140}(?:\U0001f7e1|active-not-executed|AKTİF[^;|\n]{0,50}(?:HENÜZ|henüz)|henüz yürütülmedi|HENÜZ YÜRÜTÜLMEDİ)", re.I),
    re.compile(r"(?:Aktif adım|Aktif step|Sıradaki numaralı çalışma|Sıradaki gerçek numbered work)[^\n]{0,120}\b8F\b", re.I),
    re.compile(r"\*\*(?:Son tamamlanan|Son tamamlanan numaralı adım):\*\*[^\n]{0,100}\b8E\b", re.I),
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

# Accepted 8F artifact / QA guards.
ds_path = ROOT / "ux/8f_design_system/design_system.yaml"
qa_path = ROOT / "ux/8f_design_system/qa_report.yaml"
spec_path = ROOT / "docs/DESIGN_SYSTEM_SPEC.md"
if not ds_path.exists() or not qa_path.exists() or not spec_path.exists():
    blocking.append({"file": "8F accepted artifacts", "kind": "accepted_artifact_missing"})
else:
    ds = yaml.safe_load(ds_path.read_text(encoding="utf-8"))
    qa = yaml.safe_load(qa_path.read_text(encoding="utf-8"))
    spec = spec_path.read_text(encoding="utf-8")
    if ds.get("status") != "accepted_8f" or ds.get("decision") != "D-073":
        blocking.append({"file": str(ds_path.relative_to(ROOT)), "kind": "not_accepted",
                         "status": ds.get("status"), "decision": ds.get("decision")})
    if qa.get("result") != "PASS" or qa.get("checks_failed") != 0 or qa.get("checks_passed") != 121:
        blocking.append({"file": str(qa_path.relative_to(ROOT)), "kind": "qa_not_pass", "result": qa.get("result"),
                         "passed": qa.get("checks_passed"), "failed": qa.get("checks_failed")})
    if "**Status:** ACCEPTED — independent 8F QA PASS" not in spec or "**Decision:** `D-073`" not in spec:
        blocking.append({"file": str(spec_path.relative_to(ROOT)), "kind": "spec_not_accepted"})

# Durable decision must exist.
decisions = (ROOT / "docs/DECISIONS.md").read_text(encoding="utf-8")
if "## D-073 — Visual Design System = VDSX-v0" not in decisions:
    blocking.append({"file": "docs/DECISIONS.md", "kind": "D-073_missing"})

# Prior accepted UX steps must not have been silently reopened.
for rel, status_val, decision_val in [
    ("ux/8b_today_home/home.yaml", "accepted_8b", "D-069"),
    ("ux/8c_daily_working_flow/flow.yaml", "accepted_8c", "D-070"),
    ("ux/8d_assessment_session/session.yaml", "accepted_8d", "D-071"),
    ("ux/8e_progress_skill_weakness/progress.yaml", "accepted_8e", "D-072"),
]:
    path = ROOT / rel
    if path.exists():
        data = yaml.safe_load(path.read_text(encoding="utf-8"))
        if data.get("status") != status_val or data.get("decision") != decision_val:
            blocking.append({"file": rel, "kind": "prior_step_regressed",
                             "status": data.get("status"), "decision": data.get("decision")})

# 8F must keep covering exactly the upstream state union; drift in either direction is blocking.
if ds_path.exists():
    ds = yaml.safe_load(ds_path.read_text(encoding="utf-8"))
    union: list[str] = []
    for rel in ["ux/8b_today_home/home.yaml", "ux/8c_daily_working_flow/flow.yaml",
                "ux/8d_assessment_session/session.yaml", "ux/8e_progress_skill_weakness/progress.yaml"]:
        for s in yaml.safe_load((ROOT / rel).read_text(encoding="utf-8"))["semantic_states"]:
            if s not in union:
                union.append(s)
    for s in yaml.safe_load((ROOT / "ux/8a_information_architecture/ia.yaml").read_text(encoding="utf-8"))["cross_cutting_surface_states"]:
        if s not in union:
            union.append(s)
    mapped = set(ds.get("surface_state_tones", {}))
    if mapped != set(union):
        blocking.append({"file": "8F tone coverage", "kind": "state_coverage_drift",
                         "unmapped": sorted(set(union) - mapped), "invented": sorted(mapped - set(union))})
    fault = sorted(k for k, v in ds.get("surface_state_tones", {}).items() if v == "system_fault")
    if fault != ["data_recovery_required", "error_recoverable"]:
        blocking.append({"file": "8F tone coverage", "kind": "fault_tone_leak", "fault_states": fault})

# Repo-wide inventory: stale-like 8F wording outside living sources is reported but non-blocking
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
    "stage_step": "8F",
    "model": "VDSX-v0",
    "decision": "D-073",
    "audit": "repo_wide_stale_reference_post_step",
    "result": "PASS" if not blocking else "FAIL",
    "repo_files_scanned": repo_files_scanned,
    "checked_current_files": checked,
    "historical_non_blocking_hits": historical_hits[:40],
    "blocking_findings": blocking,
    "historical_scope_policy": "Historical specs/research/logs/tools may preserve at-the-time 8F future/candidate language; current/living sources must agree on 8F complete and 8G active-not-executed.",
    "closure_gate_note": "This is a one-time closure gate per PROJECT_MEMORY_PROTOCOL 4.4. It is expected to fail once 8G completes and must not be re-run afterwards; its stored report is the durable evidence.",
}
REPORT.parent.mkdir(parents=True, exist_ok=True)
REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"8F_POST_STALE_AUDIT={report['result']}")
print(f"repo_files_scanned={repo_files_scanned} current_checked={len(checked)} "
      f"blocking={len(blocking)} historical_non_blocking={len(historical_hits)}")
for finding in blocking:
    print("-", str(finding).encode("ascii", "backslashreplace").decode("ascii"))

sys.exit(0 if not blocking else 1)
