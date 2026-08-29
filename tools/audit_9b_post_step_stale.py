from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / "arch/9b_local_first_persistence/stale_reference_audit.yaml"

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
    "AGENTS.md": ["9B: ✅ `LFPS-v0 / D-076`", "Aktif adım: 9C — Domain veri modeli"],
    "PROJECT_CONTEXT.md": ["9B ✅ Veri saklama / local-first — LFPS-v0 / D-076", "9C 🟡 Domain veri modeli — AKTİF"],
    "docs/START_HERE.md": ["9B ✅ **LFPS-v0 / D-076**", "9C — Domain veri modeli"],
    "docs/HANDOFF_STATE.md": ["9B ✅ LFPS-v0 / D-076", "Aktif:** `9C — Domain veri modeli"],
    "docs/STEP_STATUS.md": ["9B — Veri saklama / local-first** | ✅", "9C — Domain veri modeli** | 🟡 Aktif"],
    "docs/EXECUTION_INDEX.md": ["[x] **9B — Veri saklama / local-first** — `LFPS-v0 / D-076`", "**9C — Domain veri modeli** **AKTİF**"],
    "docs/MASTER_PLAN.md": ["[x] 9B — Veri saklama/local-first — LFPS-v0 / D-076", "9C — Domain veri modeli — **AKTİF**"],
    "docs/LOCAL_MANAGER_HANDOFF.md": ["AŞAMA 9B ✅ LFPS-v0 / D-076", "Aktif adım:** `9C — Domain veri modeli"],
    "vault/agent/CURRENT_CONTEXT.md": ["LFPS-v0 / D-076", "Aktif adım **9C — Domain veri modeli**"],
    "vault/agent/OPEN_LOOPS.md": ["[x] 9B Veri saklama / local-first", "[ ] 9C Domain veri modeli **AKTİF**"],
}

STALE_PATTERNS = [
    re.compile(r"\b9B\b(?:(?![;|\n]).){0,140}(?:\U0001f7e1|active-not-executed|AKTİF[^;|\n]{0,50}(?:HENÜZ|henüz)|henüz yürütülmedi|HENÜZ YÜRÜTÜLMEDİ)", re.I),
    re.compile(r"(?:Aktif adım|Aktif step|Sıradaki numaralı çalışma|Sıradaki gerçek numbered work)[^\n]{0,120}\b9B\b", re.I),
    re.compile(r"\*\*(?:Son tamamlanan|Son tamamlanan numaralı adım):\*\*[^\n]{0,100}\b9A\b", re.I),
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

# Accepted 9A artifact / QA guards.
pers_path = ROOT / "arch/9b_local_first_persistence/persistence.yaml"
qa_path = ROOT / "arch/9b_local_first_persistence/qa_report.yaml"
spec_path = ROOT / "docs/LOCAL_FIRST_PERSISTENCE_SPEC.md"
if not all(p.exists() for p in (pers_path, qa_path, spec_path)):
    blocking.append({"file": "9B accepted artifacts", "kind": "accepted_artifact_missing"})
else:
    pers = yaml.safe_load(pers_path.read_text(encoding="utf-8"))
    qa = yaml.safe_load(qa_path.read_text(encoding="utf-8"))
    spec = spec_path.read_text(encoding="utf-8")
    if pers.get("status") != "accepted_9b" or pers.get("decision") != "D-076":
        blocking.append({"file": str(pers_path.relative_to(ROOT)), "kind": "not_accepted",
                         "status": pers.get("status"), "decision": pers.get("decision")})
    if qa.get("result") != "PASS" or qa.get("checks_failed") != 0 or qa.get("checks_passed") != 100:
        blocking.append({"file": str(qa_path.relative_to(ROOT)), "kind": "qa_not_pass", "result": qa.get("result"),
                         "passed": qa.get("checks_passed"), "failed": qa.get("checks_failed")})
    if "**Status:** ACCEPTED — independent 9B QA PASS" not in spec or "**Decision:** `D-076`" not in spec:
        blocking.append({"file": str(spec_path.relative_to(ROOT)), "kind": "spec_not_accepted"})
    # The persistence guarantees most likely to be silently lost later.
    if pers.get("invariants", {}).get("evidence_is_source_of_truth") is not True:
        blocking.append({"file": "9B architecture", "kind": "evidence_no_longer_source_of_truth"})
    if pers.get("invariants", {}).get("truth_records_append_only") is not True:
        blocking.append({"file": "9B architecture", "kind": "append_only_weakened"})
    if pers.get("exposure_records", {}).get("loss_classified_as") != "data_loss":
        blocking.append({"file": "9B architecture", "kind": "exposure_loss_downgraded"})
    if pers.get("storage_engine", {}).get("orm_or_mapping_library_chosen") is not False:
        blocking.append({"file": "9B architecture", "kind": "orm_named_in_9b"})

# Durable decision must exist.
decisions = (ROOT / "docs/DECISIONS.md").read_text(encoding="utf-8")
if "## D-076 — Local-first persistence = LFPS-v0" not in decisions:
    blocking.append({"file": "docs/DECISIONS.md", "kind": "D-076_missing"})

# All of AŞAMA 8 must remain accepted.
for rel, status_val, decision_val in [
    ("ux/8b_today_home/home.yaml", "accepted_8b", "D-069"),
    ("ux/8c_daily_working_flow/flow.yaml", "accepted_8c", "D-070"),
    ("ux/8d_assessment_session/session.yaml", "accepted_8d", "D-071"),
    ("ux/8e_progress_skill_weakness/progress.yaml", "accepted_8e", "D-072"),
    ("ux/8f_design_system/design_system.yaml", "accepted_8f", "D-073"),
    ("ux/8g_wireframe_prototype/wireframe.yaml", "accepted_8g", "D-074"),
    ("arch/9a_mobile_technology/technology.yaml", "accepted_9a", "D-075"),
]:
    path = ROOT / rel
    if path.exists():
        data = yaml.safe_load(path.read_text(encoding="utf-8"))
        if data.get("status") != status_val or data.get("decision") != decision_val:
            blocking.append({"file": rel, "kind": "prior_step_regressed",
                             "status": data.get("status"), "decision": data.get("decision")})

# Repo-wide inventory: stale-like 9A wording outside living sources is reported but non-blocking
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
    "stage_step": "9B",
    "model": "LFPS-v0",
    "decision": "D-076",
    "audit": "repo_wide_stale_reference_post_step",
    "result": "PASS" if not blocking else "FAIL",
    "stage_9_in_progress": True,
    "repo_files_scanned": repo_files_scanned,
    "checked_current_files": checked,
    "historical_non_blocking_hits": historical_hits[:40],
    "blocking_findings": blocking,
    "historical_scope_policy": "Historical specs/research/logs/tools may preserve at-the-time 9B future/candidate language; current/living sources must agree on 9B complete and 9C active-not-executed.",
    "closure_gate_note": "This is a one-time closure gate per PROJECT_MEMORY_PROTOCOL 4.4. It is expected to fail once 9C completes and must not be re-run afterwards; its stored report is the durable evidence.",
}
REPORT.parent.mkdir(parents=True, exist_ok=True)
REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"9B_POST_STALE_AUDIT={report['result']}")
print(f"repo_files_scanned={repo_files_scanned} current_checked={len(checked)} "
      f"blocking={len(blocking)} historical_non_blocking={len(historical_hits)}")
for finding in blocking:
    print("-", str(finding).encode("ascii", "backslashreplace").decode("ascii"))

sys.exit(0 if not blocking else 1)
