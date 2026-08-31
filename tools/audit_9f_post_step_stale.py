from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / "arch/9f_test_strategy/stale_reference_audit.yaml"

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
    "AGENTS.md": ["9F: ✅ `TVSX-v0 / D-081`", "Aktif adım: 10A — Proje kurulumu"],
    "PROJECT_CONTEXT.md": ["9F ✅ Test stratejisi — TVSX-v0 / D-081", "10A 🟡 Proje kurulumu — AKTİF"],
    "docs/START_HERE.md": ["9F ✅ **TVSX-v0 / D-081**", "10A — Proje kurulumu"],
    "docs/HANDOFF_STATE.md": ["9F ✅ TVSX-v0 / D-081", "Aktif:** `10A — Proje kurulumu"],
    "docs/STEP_STATUS.md": ["9F — Test stratejisi** | ✅", "10A — Proje kurulumu** | 🟡 Aktif"],
    "docs/EXECUTION_INDEX.md": ["[x] **9F — Test stratejisi** — `TVSX-v0 / D-081`", "**10A — Proje kurulumu** **AKTİF**"],
    "docs/MASTER_PLAN.md": ["[x] 9F — Test stratejisi — TVSX-v0 / D-081", "10A — Proje kurulumu — **AKTİF**"],
    "docs/LOCAL_MANAGER_HANDOFF.md": ["AŞAMA 9F ✅ TVSX-v0 / D-081", "Aktif adım:** `10A — Proje kurulumu"],
    "vault/agent/CURRENT_CONTEXT.md": ["TVSX-v0 / D-081", "Aktif adım **10A — Proje kurulumu**"],
    "vault/agent/OPEN_LOOPS.md": ["[x] 9F Test stratejisi", "[ ] 10A Proje kurulumu **AKTİF**"],
}

STALE_PATTERNS = [
    re.compile(r"\b9F\b(?:(?![;|\n]).){0,140}(?:\U0001f7e1|active-not-executed|AKTİF[^;|\n]{0,50}(?:HENÜZ|henüz)|henüz yürütülmedi|HENÜZ YÜRÜTÜLMEDİ)", re.I),
    re.compile(r"(?:Aktif adım|Aktif step|Sıradaki numaralı çalışma|Sıradaki gerçek numbered work)[^\n]{0,120}\b9F\b", re.I),
    re.compile(r"\*\*(?:Son tamamlanan|Son tamamlanan numaralı adım):\*\*[^\n]{0,100}\b9E\b", re.I),
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

# Accepted 9F artifact / QA guards.
ts_path = ROOT / "arch/9f_test_strategy/test_strategy.yaml"
qa_path = ROOT / "arch/9f_test_strategy/qa_report.yaml"
spec_path = ROOT / "docs/TEST_STRATEGY_SPEC.md"
if not all(p.exists() for p in (ts_path, qa_path, spec_path)):
    blocking.append({"file": "9F accepted artifacts", "kind": "accepted_artifact_missing"})
else:
    ts = yaml.safe_load(ts_path.read_text(encoding="utf-8"))
    qa = yaml.safe_load(qa_path.read_text(encoding="utf-8"))
    spec = spec_path.read_text(encoding="utf-8")
    if ts.get("status") != "accepted_9f" or ts.get("decision") != "D-081":
        blocking.append({"file": str(ts_path.relative_to(ROOT)), "kind": "not_accepted",
                         "status": ts.get("status"), "decision": ts.get("decision")})
    if qa.get("result") != "PASS" or qa.get("checks_failed") != 0 or qa.get("checks_passed") != 288:
        blocking.append({"file": str(qa_path.relative_to(ROOT)), "kind": "qa_not_pass", "result": qa.get("result"),
                         "passed": qa.get("checks_passed"), "failed": qa.get("checks_failed")})
    if "**Status:** ACCEPTED — independent 9F QA PASS" not in spec or "**Decision:** `D-081`" not in spec:
        blocking.append({"file": str(spec_path.relative_to(ROOT)), "kind": "spec_not_accepted"})
    inv = ts.get("invariants", {})
    for key, expected in [
        ("every_accepted_invariant_has_an_owning_check", True),
        ("unowned_invariant_blocks_release", True),
        ("coverage_percentage_is_a_release_gate", False),
        ("negative_verification_required_for_prohibitions", True),
        ("append_only_verified_by_attempted_violation", True),
        ("migration_verified_against_populated_fixtures", True),
        ("null_evaluator_verified_by_building_without_adapter", True),
        ("live_ai_provider_called_in_checks", False),
        ("flaky_check_is_a_failing_check", True),
        ("retry_to_green_allowed", False),
        ("spec_validator_sweep_is_full_glob", True),
        ("new_product_semantics_introduced_by_test_strategy", False),
    ]:
        if inv.get(key) is not expected:
            blocking.append({"file": "9F test_strategy", "kind": "invariant_regressed",
                             "invariant": key, "expected": expected, "found": inv.get(key)})
    if ts.get("closes_stage_9") is not True:
        blocking.append({"file": "9F test_strategy", "kind": "stage_9_closure_lost"})
    register = ts.get("invariant_register", [])
    if len(register) < 40 or any(not e.get("owner_check") for e in register):
        blocking.append({"file": "9F test_strategy", "kind": "invariant_register_degraded",
                         "entries": len(register)})
    gate = ts.get("release_gate", {})
    if gate.get("coverage_percentage_gate") is not False or gate.get("gate_is") != "invariant_coverage":
        blocking.append({"file": "9F test_strategy", "kind": "release_gate_regressed"})
    if len(gate.get("conditions", [])) != 11:
        blocking.append({"file": "9F test_strategy", "kind": "release_gate_condition_count",
                         "count": len(gate.get("conditions", []))})

# Durable decision must exist.
decisions = (ROOT / "docs/DECISIONS.md").read_text(encoding="utf-8")
for marker, kind in [("## D-081 — Test stratejisi = TVSX-v0", "D-081_missing"),
                     ("## D-080 — Dağıtım kapsamı", "D-080_missing")]:
    if marker not in decisions:
        blocking.append({"file": "docs/DECISIONS.md", "kind": kind})

# All of AŞAMA 8 must remain accepted.
for rel, status_val, decision_val in [
    ("ux/8b_today_home/home.yaml", "accepted_8b", "D-069"),
    ("ux/8c_daily_working_flow/flow.yaml", "accepted_8c", "D-070"),
    ("ux/8d_assessment_session/session.yaml", "accepted_8d", "D-071"),
    ("ux/8e_progress_skill_weakness/progress.yaml", "accepted_8e", "D-072"),
    ("ux/8f_design_system/design_system.yaml", "accepted_8f", "D-073"),
    ("ux/8g_wireframe_prototype/wireframe.yaml", "accepted_8g", "D-074"),
    ("arch/9a_mobile_technology/technology.yaml", "accepted_9a", "D-075"),
    ("arch/9b_local_first_persistence/persistence.yaml", "accepted_9b", "D-076"),
    ("arch/9c_domain_data_model/data_model.yaml", "accepted_9c", "D-077"),
    ("arch/9d_service_boundaries/boundaries.yaml", "accepted_9d", "D-078"),
    ("arch/9e_ai_integration/ai_integration.yaml", "accepted_9e", "D-079"),
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
    "stage_step": "9F",
    "model": "TVSX-v0",
    "decision": "D-081",
    "audit": "repo_wide_stale_reference_post_step",
    "result": "PASS" if not blocking else "FAIL",
    "stage_9_closed": True,
    "repo_files_scanned": repo_files_scanned,
    "checked_current_files": checked,
    "historical_non_blocking_hits": historical_hits[:40],
    "blocking_findings": blocking,
    "historical_scope_policy": "Historical specs/research/logs/tools may preserve at-the-time 9F future/candidate language; current/living sources must agree on 9D complete and 9E active-not-executed.",
    "closure_gate_note": "This is a one-time closure gate per PROJECT_MEMORY_PROTOCOL 4.4. It is expected to fail once 10A completes and must not be re-run afterwards; its stored report is the durable evidence.",
}
REPORT.parent.mkdir(parents=True, exist_ok=True)
REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"9F_POST_STALE_AUDIT={report['result']}")
print(f"repo_files_scanned={repo_files_scanned} current_checked={len(checked)} "
      f"blocking={len(blocking)} historical_non_blocking={len(historical_hits)}")
for finding in blocking:
    print("-", str(finding).encode("ascii", "backslashreplace").decode("ascii"))

sys.exit(0 if not blocking else 1)
