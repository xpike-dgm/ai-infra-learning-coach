from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / "arch/9a_mobile_technology/stale_reference_audit.yaml"

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
    "AGENTS.md": ["9A: ✅ `AMTS-v0 / D-075`", "Aktif adım: 9B — Veri saklama / local-first"],
    "PROJECT_CONTEXT.md": ["9A ✅ Mobil teknoloji seçimi — AMTS-v0 / D-075", "9B \U0001f7e1 Veri saklama / local-first — AKTİF"],
    "docs/START_HERE.md": ["9A ✅ **AMTS-v0 / D-075**", "9B — Veri saklama / local-first"],
    "docs/HANDOFF_STATE.md": ["9A ✅ AMTS-v0 / D-075", "Aktif:** `9B — Veri saklama / local-first"],
    "docs/STEP_STATUS.md": ["9A — Mobil teknoloji seçimi** | ✅", "9B — Veri saklama / local-first** | \U0001f7e1 Aktif"],
    "docs/EXECUTION_INDEX.md": ["[x] **9A — Mobil teknoloji seçimi** — `AMTS-v0 / D-075`", "**9B — Veri saklama / local-first** **AKTİF**"],
    "docs/MASTER_PLAN.md": ["[x] 9A — Mobil teknoloji seçimi — AMTS-v0 / D-075", "9B — Veri saklama/local-first — **AKTİF**"],
    "docs/LOCAL_MANAGER_HANDOFF.md": ["AŞAMA 9A ✅ AMTS-v0 / D-075", "Aktif adım:** `9B — Veri saklama / local-first"],
    "vault/agent/CURRENT_CONTEXT.md": ["AMTS-v0 / D-075", "Aktif adım **9B — Veri saklama / local-first**"],
    "vault/agent/OPEN_LOOPS.md": ["[x] 9A Mobil teknoloji seçimi", "[ ] 9B Veri saklama / local-first **AKTİF**"],
}

STALE_PATTERNS = [
    re.compile(r"\b9A\b(?:(?![;|\n]).){0,140}(?:\U0001f7e1|active-not-executed|AKTİF[^;|\n]{0,50}(?:HENÜZ|henüz)|henüz yürütülmedi|HENÜZ YÜRÜTÜLMEDİ)", re.I),
    re.compile(r"(?:Aktif adım|Aktif step|Sıradaki numaralı çalışma|Sıradaki gerçek numbered work)[^\n]{0,120}\b9A\b", re.I),
    re.compile(r"\*\*(?:Son tamamlanan|Son tamamlanan numaralı adım):\*\*[^\n]{0,100}\b8G\b", re.I),
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
tech_path = ROOT / "arch/9a_mobile_technology/technology.yaml"
qa_path = ROOT / "arch/9a_mobile_technology/qa_report.yaml"
spec_path = ROOT / "docs/MOBILE_TECHNOLOGY_SPEC.md"
if not all(p.exists() for p in (tech_path, qa_path, spec_path)):
    blocking.append({"file": "9A accepted artifacts", "kind": "accepted_artifact_missing"})
else:
    tech = yaml.safe_load(tech_path.read_text(encoding="utf-8"))
    qa = yaml.safe_load(qa_path.read_text(encoding="utf-8"))
    spec = spec_path.read_text(encoding="utf-8")
    if tech.get("status") != "accepted_9a" or tech.get("decision") != "D-075":
        blocking.append({"file": str(tech_path.relative_to(ROOT)), "kind": "not_accepted",
                         "status": tech.get("status"), "decision": tech.get("decision")})
    if qa.get("result") != "PASS" or qa.get("checks_failed") != 0 or qa.get("checks_passed") != 100:
        blocking.append({"file": str(qa_path.relative_to(ROOT)), "kind": "qa_not_pass", "result": qa.get("result"),
                         "passed": qa.get("checks_passed"), "failed": qa.get("checks_failed")})
    if "**Status:** ACCEPTED — independent 9A QA PASS" not in spec or "**Decision:** `D-075`" not in spec:
        blocking.append({"file": str(spec_path.relative_to(ROOT)), "kind": "spec_not_accepted"})
    # The two selection guarantees most likely to be silently lost later.
    if tech.get("component_library", {}).get("dynamic_color_enabled") is not False:
        blocking.append({"file": "9A selection", "kind": "dynamic_colour_enabled"})
    core_forbidden = set(tech.get("deterministic_core", {}).get("forbidden_dependencies", []))
    if core_forbidden != {"android_api", "ui_toolkit", "networking", "ai_client"}:
        blocking.append({"file": "9A selection", "kind": "core_purity_weakened",
                         "forbidden": sorted(core_forbidden)})
    if not tech.get("verification_list", {}).get("items"):
        blocking.append({"file": "9A selection", "kind": "verification_list_empty"})

# Durable decision must exist.
decisions = (ROOT / "docs/DECISIONS.md").read_text(encoding="utf-8")
if "## D-075 — Mobil teknoloji seçimi = AMTS-v0" not in decisions:
    blocking.append({"file": "docs/DECISIONS.md", "kind": "D-075_missing"})

# All of AŞAMA 8 must remain accepted.
for rel, status_val, decision_val in [
    ("ux/8b_today_home/home.yaml", "accepted_8b", "D-069"),
    ("ux/8c_daily_working_flow/flow.yaml", "accepted_8c", "D-070"),
    ("ux/8d_assessment_session/session.yaml", "accepted_8d", "D-071"),
    ("ux/8e_progress_skill_weakness/progress.yaml", "accepted_8e", "D-072"),
    ("ux/8f_design_system/design_system.yaml", "accepted_8f", "D-073"),
    ("ux/8g_wireframe_prototype/wireframe.yaml", "accepted_8g", "D-074"),
]:
    path = ROOT / rel
    if path.exists():
        data = yaml.safe_load(path.read_text(encoding="utf-8"))
        if data.get("status") != status_val or data.get("decision") != decision_val:
            blocking.append({"file": rel, "kind": "stage8_step_regressed",
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
    "stage_step": "9A",
    "model": "AMTS-v0",
    "decision": "D-075",
    "audit": "repo_wide_stale_reference_post_step",
    "result": "PASS" if not blocking else "FAIL",
    "stage_9_opened": True,
    "repo_files_scanned": repo_files_scanned,
    "checked_current_files": checked,
    "historical_non_blocking_hits": historical_hits[:40],
    "blocking_findings": blocking,
    "historical_scope_policy": "Historical specs/research/logs/tools may preserve at-the-time 9A future/candidate language; current/living sources must agree on 9A complete and 9B active-not-executed.",
    "closure_gate_note": "This is a one-time closure gate per PROJECT_MEMORY_PROTOCOL 4.4. It is expected to fail once 9B completes and must not be re-run afterwards; its stored report is the durable evidence.",
}
REPORT.parent.mkdir(parents=True, exist_ok=True)
REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"9A_POST_STALE_AUDIT={report['result']}")
print(f"repo_files_scanned={repo_files_scanned} current_checked={len(checked)} "
      f"blocking={len(blocking)} historical_non_blocking={len(historical_hits)}")
for finding in blocking:
    print("-", str(finding).encode("ascii", "backslashreplace").decode("ascii"))

sys.exit(0 if not blocking else 1)
