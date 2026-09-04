from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / "arch/10b_navigation/stale_reference_audit.yaml"

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
    "AGENTS.md": ["10B: ✅ `NSHX-v0 / D-083`", "Aktif adım: 10C — Design system implementation"],
    "PROJECT_CONTEXT.md": ["10B ✅ Navigation — NSHX-v0 / D-083", "10C 🟡 Design system implementation — AKTİF"],
    "docs/START_HERE.md": ["10B ✅ **NSHX-v0 / D-083**", "10C — Design system implementation"],
    "docs/HANDOFF_STATE.md": ["10B ✅ NSHX-v0 / D-083", "Aktif:** `10C — Design system implementation"],
    "docs/STEP_STATUS.md": ["10B — Navigation** | ✅", "10C — Design system implementation** | 🟡 Aktif"],
    "docs/EXECUTION_INDEX.md": ["[x] **10B — Navigation** — `NSHX-v0 / D-083`", "**10C — Design system implementation** **AKTİF**"],
    "docs/MASTER_PLAN.md": ["[x] 10B — Navigation — NSHX-v0 / D-083", "10C — Design system implementation — **AKTİF**"],
    "docs/LOCAL_MANAGER_HANDOFF.md": ["AŞAMA 10B ✅ NSHX-v0 / D-083", "Aktif adım:** `10C — Design system implementation"],
    "vault/agent/CURRENT_CONTEXT.md": ["NSHX-v0 / D-083", "Aktif adım **10C — Design system implementation**"],
    "vault/agent/OPEN_LOOPS.md": ["[x] 10B Navigation", "[ ] 10C Design system implementation **AKTİF**"],
}

STALE_PATTERNS = [
    re.compile(r"\b10B\b(?:(?![;|\n]).){0,140}(?:\U0001f7e1|active-not-executed|AKTİF[^;|\n]{0,50}(?:HENÜZ|henüz)|henüz yürütülmedi|HENÜZ YÜRÜTÜLMEDİ)", re.I),
    re.compile(r"(?:Aktif adım|Aktif step|Sıradaki numaralı çalışma|Sıradaki gerçek numbered work)[^\n]{0,120}\b10B\b", re.I),
    re.compile(r"\*\*(?:Son tamamlanan|Son tamamlanan numaralı adım):\*\*[^\n]{0,100}\b10A\b", re.I),
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

# Accepted 10B artifact / QA guards.
nav_path = ROOT / "arch/10b_navigation/navigation.yaml"
qa_path = ROOT / "arch/10b_navigation/qa_report.yaml"
spec_path = ROOT / "docs/NAVIGATION_SHELL_SPEC.md"
if not all(p.exists() for p in (nav_path, qa_path, spec_path)):
    blocking.append({"file": "10B accepted artifacts", "kind": "accepted_artifact_missing"})
else:
    nav = yaml.safe_load(nav_path.read_text(encoding="utf-8"))
    qa = yaml.safe_load(qa_path.read_text(encoding="utf-8"))
    spec = spec_path.read_text(encoding="utf-8")
    if nav.get("status") != "accepted_10b" or nav.get("decision") != "D-083":
        blocking.append({"file": str(nav_path.relative_to(ROOT)), "kind": "not_accepted",
                         "status": nav.get("status"), "decision": nav.get("decision")})
    if qa.get("result") != "PASS" or qa.get("checks_failed") != 0 or qa.get("checks_passed") != 104:
        blocking.append({"file": str(qa_path.relative_to(ROOT)), "kind": "qa_not_pass", "result": qa.get("result"),
                         "passed": qa.get("checks_passed"), "failed": qa.get("checks_failed")})
    if "**Status:** ACCEPTED — independent 10B QA PASS" not in spec or "**Decision:** `D-083`" not in spec:
        blocking.append({"file": str(spec_path.relative_to(ROOT)), "kind": "spec_not_accepted"})
    if nav.get("model_location", {}).get("module") != "core-presentation" or             nav.get("model_location", {}).get("rules_in_ui_toolkit") is not False:
        blocking.append({"file": "10B navigation", "kind": "model_location_regressed"})
    if nav.get("destinations", {}).get("ids") != ["today", "learn", "progress", "profile"]:
        blocking.append({"file": "10B navigation", "kind": "destination_set_regressed"})
    if nav.get("forbidden_top_level_present") is not False or             nav.get("unlisted_transition_navigable") is not False:
        blocking.append({"file": "10B navigation", "kind": "ia_guard_regressed"})
    if nav.get("focused_flow", {}).get("unsafe_state_representable") is not False:
        blocking.append({"file": "10B navigation", "kind": "unsafe_focused_flow_state_became_representable"})
    if nav.get("surfaces", {}).get("duplicate_skill_detail_possible") is not False:
        blocking.append({"file": "10B navigation", "kind": "shared_surface_identity_lost"})
    if nav.get("window_classes", {}).get("destination_set_changes_with_window_class") is not False:
        blocking.append({"file": "10B navigation", "kind": "adaptive_created_second_ia"})
    if not (ROOT / "android/core-presentation/src/main/kotlin/coach/presentation/Navigation.kt").exists():
        blocking.append({"file": "android", "kind": "navigation_model_missing"})

# Durable decision must exist.
decisions = (ROOT / "docs/DECISIONS.md").read_text(encoding="utf-8")
for marker, kind in [("## D-083 — Navigation = NSHX-v0", "D-083_missing"),
                     ("## D-082 — Proje kurulumu = MPSX-v0", "D-082_missing"),
                     ("## D-081 — Test stratejisi = TVSX-v0", "D-081_missing"),
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
    ("arch/9f_test_strategy/test_strategy.yaml", "accepted_9f", "D-081"),
    ("arch/10a_project_setup/project_setup.yaml", "accepted_10a", "D-082"),
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
# Generated Gradle output is not repository memory; scanning it would add thousands of
# files and could report a stale phrase from a build artifact as if it were a source.
IGNORED_PARTS = {".git", "build", ".gradle", ".kotlin"}
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
    "stage_step": "10B",
    "model": "NSHX-v0",
    "decision": "D-083",
    "audit": "repo_wide_stale_reference_post_step",
    "result": "PASS" if not blocking else "FAIL",
    "stage_9_closed": True,
    "stage_10_in_progress": True,
    "repo_files_scanned": repo_files_scanned,
    "checked_current_files": checked,
    "historical_non_blocking_hits": historical_hits[:40],
    "blocking_findings": blocking,
    "historical_scope_policy": "Historical specs/research/logs/tools may preserve at-the-time 10B future/candidate language; current/living sources must agree on 9D complete and 9E active-not-executed.",
    "closure_gate_note": "This is a one-time closure gate per PROJECT_MEMORY_PROTOCOL 4.4. It is expected to fail once 10C completes and must not be re-run afterwards; its stored report is the durable evidence.",
}
REPORT.parent.mkdir(parents=True, exist_ok=True)
REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True), encoding="utf-8")

print(f"10B_POST_STALE_AUDIT={report['result']}")
print(f"repo_files_scanned={repo_files_scanned} current_checked={len(checked)} "
      f"blocking={len(blocking)} historical_non_blocking={len(historical_hits)}")
for finding in blocking:
    print("-", str(finding).encode("ascii", "backslashreplace").decode("ascii"))

sys.exit(0 if not blocking else 1)
