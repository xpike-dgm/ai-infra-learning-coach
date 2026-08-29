from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / "ux/8g_wireframe_prototype/stale_reference_audit.yaml"

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
    "AGENTS.md": ["8G: ✅ `WFPX-v0 / D-074`", "Aktif adım: 9A — Mobil teknoloji seçimi", "AŞAMA 8 TAMAMLANDI"],
    "PROJECT_CONTEXT.md": ["8G ✅ Wireframe/prototip — WFPX-v0 / D-074", "9A \U0001f7e1 Mobil teknoloji seçimi — AKTİF"],
    "docs/START_HERE.md": ["8G ✅ WFPX-v0 / D-074", "9A — Mobil teknoloji seçimi"],
    "docs/HANDOFF_STATE.md": ["8G ✅ WFPX-v0 / D-074", "Aktif:** `9A — Mobil teknoloji seçimi"],
    "docs/STEP_STATUS.md": ["8G — Wireframe/prototip** | ✅", "9A — Mobil teknoloji seçimi** | \U0001f7e1 Aktif"],
    "docs/EXECUTION_INDEX.md": ["[x] **8G — Wireframe/prototip** — `WFPX-v0 / D-074`", "**9A — Mobil teknoloji seçimi** **AKTİF**"],
    "docs/MASTER_PLAN.md": ["[x] 8G — Wireframe/prototip — WFPX-v0 / D-074", "AŞAMA 8 kapandı"],
    "docs/LOCAL_MANAGER_HANDOFF.md": ["AŞAMA 8G ✅ WFPX-v0 / D-074", "Aktif adım:** `9A — Mobil teknoloji seçimi"],
    "vault/agent/CURRENT_CONTEXT.md": ["WFPX-v0 / D-074", "Aktif adım **9A — Mobil teknoloji seçimi**"],
    "vault/agent/OPEN_LOOPS.md": ["[x] 8G Wireframe/prototip", "[ ] 9A Mobil teknoloji seçimi **AKTİF**"],
}

STALE_PATTERNS = [
    re.compile(r"\b8G\b(?:(?![;|\n]).){0,140}(?:\U0001f7e1|active-not-executed|AKTİF[^;|\n]{0,50}(?:HENÜZ|henüz)|henüz yürütülmedi|HENÜZ YÜRÜTÜLMEDİ)", re.I),
    re.compile(r"(?:Aktif adım|Aktif step|Sıradaki numaralı çalışma|Sıradaki gerçek numbered work)[^\n]{0,120}\b8G\b", re.I),
    re.compile(r"\*\*(?:Son tamamlanan|Son tamamlanan numaralı adım):\*\*[^\n]{0,100}\b8F\b", re.I),
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

# Accepted 8G artifact / QA guards.
wf_path = ROOT / "ux/8g_wireframe_prototype/wireframe.yaml"
qa_path = ROOT / "ux/8g_wireframe_prototype/qa_report.yaml"
spec_path = ROOT / "docs/WIREFRAME_PROTOTYPE_SPEC.md"
proto_path = ROOT / "ux/8g_wireframe_prototype/prototype.html"
if not all(p.exists() for p in (wf_path, qa_path, spec_path, proto_path)):
    blocking.append({"file": "8G accepted artifacts", "kind": "accepted_artifact_missing"})
else:
    wf = yaml.safe_load(wf_path.read_text(encoding="utf-8"))
    qa = yaml.safe_load(qa_path.read_text(encoding="utf-8"))
    spec = spec_path.read_text(encoding="utf-8")
    if wf.get("status") != "accepted_8g" or wf.get("decision") != "D-074":
        blocking.append({"file": str(wf_path.relative_to(ROOT)), "kind": "not_accepted",
                         "status": wf.get("status"), "decision": wf.get("decision")})
    if qa.get("result") != "PASS" or qa.get("checks_failed") != 0 or qa.get("checks_passed") != 222:
        blocking.append({"file": str(qa_path.relative_to(ROOT)), "kind": "qa_not_pass", "result": qa.get("result"),
                         "passed": qa.get("checks_passed"), "failed": qa.get("checks_failed")})
    if qa.get("contrast_pairs_measured") != 52:
        blocking.append({"file": str(qa_path.relative_to(ROOT)), "kind": "contrast_not_measured",
                         "pairs": qa.get("contrast_pairs_measured")})
    if "**Status:** ACCEPTED — independent 8G QA PASS" not in spec or "**Decision:** `D-074`" not in spec:
        blocking.append({"file": str(spec_path.relative_to(ROOT)), "kind": "spec_not_accepted"})

# Durable decision must exist.
decisions = (ROOT / "docs/DECISIONS.md").read_text(encoding="utf-8")
if "## D-074 — Wireframe & Prototype Geometry = WFPX-v0" not in decisions:
    blocking.append({"file": "docs/DECISIONS.md", "kind": "D-074_missing"})

# Every prior Stage 8 step must remain accepted; 8G closes the stage.
for rel, status_val, decision_val in [
    ("ux/8b_today_home/home.yaml", "accepted_8b", "D-069"),
    ("ux/8c_daily_working_flow/flow.yaml", "accepted_8c", "D-070"),
    ("ux/8d_assessment_session/session.yaml", "accepted_8d", "D-071"),
    ("ux/8e_progress_skill_weakness/progress.yaml", "accepted_8e", "D-072"),
    ("ux/8f_design_system/design_system.yaml", "accepted_8f", "D-073"),
]:
    path = ROOT / rel
    if path.exists():
        data = yaml.safe_load(path.read_text(encoding="utf-8"))
        if data.get("status") != status_val or data.get("decision") != decision_val:
            blocking.append({"file": rel, "kind": "prior_step_regressed",
                             "status": data.get("status"), "decision": data.get("decision")})

# Repo-wide inventory: stale-like 8G wording outside living sources is reported but non-blocking
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
    "stage_step": "8G",
    "model": "WFPX-v0",
    "decision": "D-074",
    "audit": "repo_wide_stale_reference_post_step",
    "result": "PASS" if not blocking else "FAIL",
    "stage_8_closed": True,
    "repo_files_scanned": repo_files_scanned,
    "checked_current_files": checked,
    "historical_non_blocking_hits": historical_hits[:40],
    "blocking_findings": blocking,
    "historical_scope_policy": "Historical specs/research/logs/tools may preserve at-the-time 8G future/candidate language; current/living sources must agree on 8G complete, AŞAMA 8 closed and 9A active-not-executed.",
    "closure_gate_note": "This is a one-time closure gate per PROJECT_MEMORY_PROTOCOL 4.4. It is expected to fail once 9A completes and must not be re-run afterwards; its stored report is the durable evidence.",
}
REPORT.parent.mkdir(parents=True, exist_ok=True)
REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"8G_POST_STALE_AUDIT={report['result']}")
print(f"repo_files_scanned={repo_files_scanned} current_checked={len(checked)} "
      f"blocking={len(blocking)} historical_non_blocking={len(historical_hits)}")
for finding in blocking:
    print("-", str(finding).encode("ascii", "backslashreplace").decode("ascii"))

sys.exit(0 if not blocking else 1)
