from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / "arch/9e_ai_integration/stale_reference_audit.yaml"

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
    "AGENTS.md": ["9E: ✅ `AIAX-v0 / D-079`", "Aktif adım: 9F — Test stratejisi"],
    "PROJECT_CONTEXT.md": ["9E ✅ AI entegrasyon mimarisi — AIAX-v0 / D-079", "9F 🟡 Test stratejisi — AKTİF"],
    "docs/START_HERE.md": ["9E ✅ AIAX-v0 / D-079", "9F — Test stratejisi"],
    "docs/HANDOFF_STATE.md": ["9E ✅ AIAX-v0 / D-079", "Aktif:** `9F — Test stratejisi"],
    "docs/STEP_STATUS.md": ["9E — AI entegrasyon mimarisi** | ✅", "9F — Test stratejisi** | 🟡 Aktif"],
    "docs/EXECUTION_INDEX.md": ["[x] **9E — AI entegrasyon mimarisi** — `AIAX-v0 / D-079`", "**9F — Test stratejisi** **AKTİF**"],
    "docs/MASTER_PLAN.md": ["[x] 9E — AI entegrasyon mimarisi — AIAX-v0 / D-079", "9F — Test stratejisi — **AKTİF**"],
    "docs/LOCAL_MANAGER_HANDOFF.md": ["AŞAMA 9E ✅ AIAX-v0 / D-079", "Aktif adım:** `9F — Test stratejisi"],
    "vault/agent/CURRENT_CONTEXT.md": ["AIAX-v0 / D-079", "Aktif adım **9F — Test stratejisi**"],
    "vault/agent/OPEN_LOOPS.md": ["[x] 9E AI entegrasyon mimarisi", "[ ] 9F Test stratejisi **AKTİF**"],
}

STALE_PATTERNS = [
    re.compile(r"\b9E\b(?:(?![;|\n]).){0,140}(?:\U0001f7e1|active-not-executed|AKTİF[^;|\n]{0,50}(?:HENÜZ|henüz)|henüz yürütülmedi|HENÜZ YÜRÜTÜLMEDİ)", re.I),
    re.compile(r"(?:Aktif adım|Aktif step|Sıradaki numaralı çalışma|Sıradaki gerçek numbered work)[^\n]{0,120}\b9E\b", re.I),
    re.compile(r"\*\*(?:Son tamamlanan|Son tamamlanan numaralı adım):\*\*[^\n]{0,100}\b9D\b", re.I),
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

# Accepted 9E artifact / QA guards.
ai_path = ROOT / "arch/9e_ai_integration/ai_integration.yaml"
qa_path = ROOT / "arch/9e_ai_integration/qa_report.yaml"
spec_path = ROOT / "docs/AI_INTEGRATION_ARCHITECTURE_SPEC.md"
if not all(p.exists() for p in (ai_path, qa_path, spec_path)):
    blocking.append({"file": "9E accepted artifacts", "kind": "accepted_artifact_missing"})
else:
    ai = yaml.safe_load(ai_path.read_text(encoding="utf-8"))
    qa = yaml.safe_load(qa_path.read_text(encoding="utf-8"))
    spec = spec_path.read_text(encoding="utf-8")
    if ai.get("status") != "accepted_9e" or ai.get("decision") != "D-079":
        blocking.append({"file": str(ai_path.relative_to(ROOT)), "kind": "not_accepted",
                         "status": ai.get("status"), "decision": ai.get("decision")})
    if qa.get("result") != "PASS" or qa.get("checks_failed") != 0 or qa.get("checks_passed") != 86:
        blocking.append({"file": str(qa_path.relative_to(ROOT)), "kind": "qa_not_pass", "result": qa.get("result"),
                         "passed": qa.get("checks_passed"), "failed": qa.get("checks_failed")})
    if "**Status:** ACCEPTED — independent 9E QA PASS" not in spec or "**Decision:** `D-079`" not in spec:
        blocking.append({"file": str(spec_path.relative_to(ROOT)), "kind": "spec_not_accepted"})
    # The AI-integration guarantees most likely to be silently lost later.
    inv = ai.get("invariants", {})
    for key, expected in [
        ("refusal_treated_as_wrong_answer", False),
        ("refusal_produces_negative_evidence", False),
        ("hardcoded_or_shared_key_in_apk", False),
        ("backend_proxy_in_v1", False),
        ("free_text_parsing_of_verdict", False),
        ("deterministic_work_calls_ai", False),
        ("uncalibrated_llm_evaluation_is_verified", False),
        ("evidence_history_sent_to_provider", False),
        ("mastery_state_sent_to_provider", False),
        ("ai_proposes_engines_decide", True),
        ("evaluator_output_schema_constrained", True),
        ("non_answer_yields_evaluation_pending", True),
        ("timeout_budget_is_end_to_end", True),
        ("app_usable_with_ai_disabled", True),
    ]:
        if inv.get(key) is not expected:
            blocking.append({"file": "9E ai_integration", "kind": "invariant_regressed",
                             "invariant": key, "expected": expected, "found": inv.get(key)})
    outcomes = {o["id"]: o for o in ai.get("outcome_taxonomy", {}).get("outcomes", [])}
    for oid in ("refused", "timed_out", "transport_error", "invalid_response", "unavailable"):
        o = outcomes.get(oid, {})
        if o.get("writes_evidence") is not False or o.get("degrades_to") != "evaluation_pending":
            blocking.append({"file": "9E ai_integration", "kind": "non_answer_outcome_regressed", "outcome": oid})
    never = set(ai.get("ai_may_never", []))
    for item in ("write_or_change_mastery_state", "satisfy_or_bypass_prerequisite",
                 "change_planner_priority_rank_or_capacity", "validate_its_own_generated_item",
                 "turn_refusal_timeout_or_error_into_negative_result"):
        if item not in never:
            blocking.append({"file": "9E ai_integration", "kind": "ai_authority_bound_lost", "missing": item})
    never_sent = set(ai.get("privacy", {}).get("never_sent", []))
    for item in ("evidence_history", "mastery_state", "plan", "profile", "exposure_records",
                 "provenance", "planner_traces"):
        if item not in never_sent:
            blocking.append({"file": "9E ai_integration", "kind": "privacy_boundary_narrowed", "missing": item})

# Durable decision must exist.
decisions = (ROOT / "docs/DECISIONS.md").read_text(encoding="utf-8")
if "## D-079 — AI entegrasyon mimarisi = AIAX-v0" not in decisions:
    blocking.append({"file": "docs/DECISIONS.md", "kind": "D-079_missing"})

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
    "stage_step": "9E",
    "model": "AIAX-v0",
    "decision": "D-079",
    "audit": "repo_wide_stale_reference_post_step",
    "result": "PASS" if not blocking else "FAIL",
    "stage_9_in_progress": True,
    "repo_files_scanned": repo_files_scanned,
    "checked_current_files": checked,
    "historical_non_blocking_hits": historical_hits[:40],
    "blocking_findings": blocking,
    "historical_scope_policy": "Historical specs/research/logs/tools may preserve at-the-time 9E future/candidate language; current/living sources must agree on 9D complete and 9E active-not-executed.",
    "closure_gate_note": "This is a one-time closure gate per PROJECT_MEMORY_PROTOCOL 4.4. It is expected to fail once 9F completes and must not be re-run afterwards; its stored report is the durable evidence.",
}
REPORT.parent.mkdir(parents=True, exist_ok=True)
REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"9E_POST_STALE_AUDIT={report['result']}")
print(f"repo_files_scanned={repo_files_scanned} current_checked={len(checked)} "
      f"blocking={len(blocking)} historical_non_blocking={len(historical_hits)}")
for finding in blocking:
    print("-", str(finding).encode("ascii", "backslashreplace").decode("ascii"))

sys.exit(0 if not blocking else 1)
