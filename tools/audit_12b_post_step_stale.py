from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / "arch/12b_prerequisite_engine/stale_reference_audit.yaml"

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
    "AGENTS.md": ["12B: ✅ `PRQX-v0 / D-093`", "Aktif adım: 12C — Planner Engine v1"],
    "PROJECT_CONTEXT.md": ["12B ✅ Prerequisite Engine — PRQX-v0 / D-093", "12C 🟡 Planner Engine v1 — AKTİF", "## 12.19 12B"],
    "docs/START_HERE.md": ["12B ✅ **PRQX-v0 / D-093**", "12C — Planner Engine v1", "### D-093 — PRQX-v0"],
    "docs/HANDOFF_STATE.md": ["12B ✅ PRQX-v0 / D-093", "Aktif:** `12C — Planner Engine v1", "## 46. 12C handoff"],
    "docs/STEP_STATUS.md": ["12B — Prerequisite Engine** | ✅", "12C — Planner Engine v1** | 🟡 Aktif"],
    "docs/EXECUTION_INDEX.md": ["[x] **12B — Prerequisite Engine** — `PRQX-v0 / D-093`", "**12C — Planner Engine v1** **AKTİF**"],
    "docs/MASTER_PLAN.md": ["[x] 12B — Prerequisite Engine — PRQX-v0 / D-093", "**Aktif:** **`12C — Planner Engine v1`**"],
    "docs/LOCAL_MANAGER_HANDOFF.md": ["AŞAMA 12B ✅ PRQX-v0 / D-093", "Aktif adım:** `12C — Planner Engine v1", "## 12B completion addendum — D-093"],
    "vault/agent/CURRENT_CONTEXT.md": ["PRQX-v0 / D-093", "Aktif adım **12C — Planner Engine v1**"],
    "vault/agent/OPEN_LOOPS.md": ["[x] 12B Prerequisite Engine", "[ ] 12C Planner Engine v1 **AKTİF**"],
}

STALE_PATTERNS = [
    re.compile(r"\b12B\b(?:(?![;|\n]).){0,140}(?:\U0001f7e1|active-not-executed|AKTİF[^;|\n]{0,50}(?:HENÜZ|henüz)|henüz yürütülmedi|HENÜZ YÜRÜTÜLMEDİ)", re.I),
    re.compile(r"(?:Aktif adım|Aktif step|Sıradaki numaralı çalışma|Sıradaki gerçek numbered work)[^\n]{0,120}\b12B\b", re.I),
    re.compile(r"\*\*(?:Son tamamlanan|Son tamamlanan numaralı adım):\*\*[^\n]{0,100}\b12A\b", re.I),
    re.compile(r"AŞAMA 12C–20 bekliyor"),
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

# Accepted 12B artifact / QA guards.
contract_path = ROOT / "arch/12b_prerequisite_engine/prerequisite_engine.yaml"
qa_path = ROOT / "arch/12b_prerequisite_engine/qa_report.yaml"
spec_path = ROOT / "docs/PREREQUISITE_ENGINE_IMPL_SPEC.md"
if not all(p.exists() for p in (contract_path, qa_path, spec_path)):
    blocking.append({"file": "12B accepted artifacts", "kind": "accepted_artifact_missing"})
else:
    contract = yaml.safe_load(contract_path.read_text(encoding="utf-8"))
    qa = yaml.safe_load(qa_path.read_text(encoding="utf-8"))
    spec = spec_path.read_text(encoding="utf-8")
    if contract.get("status") != "accepted_12b" or contract.get("decision") != "D-093":
        blocking.append({"file": str(contract_path.relative_to(ROOT)), "kind": "not_accepted",
                         "status": contract.get("status"), "decision": contract.get("decision")})
    if qa.get("result") != "PASS" or qa.get("checks_failed") != 0 or qa.get("checks_passed") != qa.get("checks_total"):
        blocking.append({"file": str(qa_path.relative_to(ROOT)), "kind": "qa_not_pass", "result": qa.get("result"),
                         "passed": qa.get("checks_passed"), "failed": qa.get("checks_failed")})
    if "**Status:** ACCEPTED — independent 12B QA PASS" not in spec or "**Decision:** `D-093`" not in spec:
        blocking.append({"file": str(spec_path.relative_to(ROOT)), "kind": "spec_not_accepted"})
    for section, key in [("scope", "interfaces_added"), ("scope", "schema_changed"), ("scope", "index_added"),
                         ("scope", "planner_implemented"), ("scope", "skill_state_axis_written"),
                         ("readiness", "is_a_score"), ("readiness", "review_due_is_not_ready"),
                         ("readiness", "unevaluated_axis_read_as_bad_news"),
                         ("readiness", "remediation_closed_without_evidence_unblocks"),
                         ("eligibility", "soft_gap_blocks"), ("eligibility", "priority_is_an_input"),
                         ("eligibility", "blocked_is_failed"), ("eligibility", "english_hidden_prerequisite"),
                         ("graph", "retired_edge_gates"), ("projection", "writes_truth"),
                         ("projection", "writes_skill_state")]:
        if contract.get(section, {}).get(key) is not False:
            blocking.append({"file": "12B prerequisite_engine", "kind": "regressed:" + section + "." + key})
    if contract.get("device_verification", {}).get("claimed") is not False:
        blocking.append({"file": "12B prerequisite_engine", "kind": "device_result_claimed"})
    if contract.get("mutation_results", {}).get("detected") != contract.get("mutation_results", {}).get("total"):
        blocking.append({"file": "12B prerequisite_engine", "kind": "mutation_escaped"})
    for rel in ("android/core-engines/src/main/kotlin/coach/engines/PrerequisiteEngine.kt",
                "android/core-model/src/main/kotlin/coach/PrerequisiteFacts.kt",
                "android/core-application/src/main/kotlin/coach/application/ResolvePrerequisites.kt",
                "android/core-engines/src/main/kotlin/coach/engines/MasteryEngine.kt"):
        if not (ROOT / rel).exists():
            blocking.append({"file": rel, "kind": "implementation_missing"})

# Structural living-memory guard (12B): a step heading appears once in MASTER_PLAN.
plan = (ROOT / "docs/MASTER_PLAN.md").read_text(encoding="utf-8")
plan_steps = re.findall(r"^### \[[ x]\] (\d+[A-Z]) ", plan, re.M)
repeated = sorted({h for h in plan_steps if plan_steps.count(h) > 1})
if repeated:
    blocking.append({"file": "docs/MASTER_PLAN.md", "kind": "repeated_step_heading", "steps": repeated})

# Durable decisions must exist.
decisions = (ROOT / "docs/DECISIONS.md").read_text(encoding="utf-8")
for marker, kind in [("## D-093 — Prerequisite Engine = PRQX-v0", "D-093_missing"),
                     ("## D-092 — Mastery Engine v1 = MSTX-v0", "D-092_missing"),
                     ("## D-091 — Gün sonu = EODX-v0", "D-091_missing"),
                     ("## D-090 — Günlük mikro quiz = DMAX-v0", "D-090_missing"),
                     ("## D-089 — Session state = SESX-v0", "D-089_missing"),
                     ("## D-088 — Task runner = RNRX-v0", "D-088_missing"),
                     ("## D-087 \u2014 Today ekran\u0131 = TDYX-v0", "D-087_missing"),
                     ("## D-086 — Temel uygulama sağlığı = APHX-v0", "D-086_missing"),
                     ("## D-085 — Local database = LDBX-v0", "D-085_missing"),
                     ("## D-084 — Design system implementation = DSIX-v0", "D-084_missing"),
                     ("## D-083 — Navigation = NSHX-v0", "D-083_missing"),
                     ("## D-082 — Proje kurulumu = MPSX-v0", "D-082_missing"),
                     ("## D-081 — Test stratejisi = TVSX-v0", "D-081_missing"),
                     ("## D-080 — Dağıtım kapsamı", "D-080_missing")]:
    if marker not in decisions:
        blocking.append({"file": "docs/DECISIONS.md", "kind": kind})

# AŞAMA 8, 9, 10, 11 and 12A must remain accepted.
for rel, status_val, decision_val in [
    ("ux/8a_information_architecture/ia.yaml", "accepted_8a", "D-068"),
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
    ("arch/9f_test_strategy/test_strategy.yaml", "accepted_9f", "D-081"),
    ("arch/10a_project_setup/project_setup.yaml", "accepted_10a", "D-082"),
    ("arch/10b_navigation/navigation.yaml", "accepted_10b", "D-083"),
    ("arch/10c_design_system/design_system_impl.yaml", "accepted_10c", "D-084"),
    ("arch/10d_local_database/local_database.yaml", "accepted_10d", "D-085"),
    ("arch/10e_app_health/app_health.yaml", "accepted_10e", "D-086"),
    ("arch/11a_today/today_interior.yaml", "accepted_11a", "D-087"),
    ("arch/11b_task_runner/task_runner.yaml", "accepted_11b", "D-088"),
    ("arch/11c_session_state/session_state.yaml", "accepted_11c", "D-089"),
    ("arch/11d_daily_micro_assessment/daily_micro.yaml", "accepted_11d", "D-090"),
    ("arch/11e_end_of_day/end_of_day.yaml", "accepted_11e", "D-091"),
    ("arch/12a_mastery_engine/mastery_engine.yaml", "accepted_12a", "D-092"),
]:
    path = ROOT / rel
    if path.exists():
        data = yaml.safe_load(path.read_text(encoding="utf-8"))
        if data.get("status") != status_val or data.get("decision") != decision_val:
            blocking.append({"file": rel, "kind": "prior_step_regressed",
                             "status": data.get("status"), "decision": data.get("decision")})
    else:
        blocking.append({"file": rel, "kind": "prior_step_contract_missing"})

# Repo-wide inventory: stale-like wording outside living sources is reported but non-blocking when it
# is preserved in historical logs, spec candidates or tooling. Current sources above are strict.
historical_hits: list[dict] = []
current_set = {str((ROOT / x).resolve()) for x in CURRENT}
repo_files_scanned = 0
# Generated Gradle output is not repository memory; scanning it would add thousands of files and
# could report a stale phrase from a build artifact as if it were a source.
# `.claude/` holds other agent sessions' worktrees — whole other checkouts, gitignored. Scanning them
# counted another branch's files as this repository's memory (12B found 1135 of them in this checkout).
IGNORED_PARTS = {".git", "build", ".gradle", ".kotlin", ".claude"}
for path in ROOT.rglob("*"):
    if not path.is_file() or IGNORED_PARTS & set(path.parts):
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
    "stage_step": "12B",
    "model": "PRQX-v0",
    "decision": "D-093",
    "audit": "repo_wide_stale_reference_post_step",
    "result": "PASS" if not blocking else "FAIL",
    "stage_9_closed": True,
    "stage_10_closed": True,
    "stage_11_closed": True,
    "repo_files_scanned": repo_files_scanned,
    "checked_current_files": checked,
    "historical_non_blocking_hits": historical_hits[:40],
    "blocking_findings": blocking,
    "historical_scope_policy": "Historical specs/research/logs/tools may preserve at-the-time future/candidate language; current/living sources must agree on 12B complete and 12C active-not-executed.",
    "closure_gate_note": "This is a one-time closure gate per PROJECT_MEMORY_PROTOCOL 4.4. It is expected to fail once 12C completes and must not be re-run afterwards; its stored report is the durable evidence.",
}
REPORT.parent.mkdir(parents=True, exist_ok=True)
REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"12B_POST_STALE_AUDIT={report['result']}")
print(f"repo_files_scanned={repo_files_scanned} current_checked={len(checked)} "
      f"blocking={len(blocking)} historical_non_blocking={len(historical_hits)}")
for finding in blocking:
    print("-", str(finding).encode("ascii", "backslashreplace").decode("ascii"))

sys.exit(0 if not blocking else 1)
