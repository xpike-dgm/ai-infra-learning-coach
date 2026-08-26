from __future__ import annotations

from pathlib import Path
import subprocess
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
failures: list[str] = []


def check(cond: bool, msg: str) -> None:
    if not cond:
        failures.append(msg)


def text(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


# Mandatory living memory coherence.
core = [
    "docs/EXECUTION_INDEX.md", "docs/STEP_STATUS.md", "docs/HANDOFF_STATE.md",
    "PROJECT_CONTEXT.md", "docs/MASTER_PLAN.md", "docs/START_HERE.md",
]
for path in core:
    t = text(path)
    check("6G" in t and ("WLRM-v0" in t or "D-061" in t), f"{path}: 6G completion missing")
    check("6H" in t, f"{path}: 6H current step missing")

check("## D-061 — Weakness localization/remediation overlay = WLRM-v0" in text("docs/DECISIONS.md"), "D-061 missing")
check((ROOT / "docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md").is_file(), "WLRM summary missing")
check((ROOT / "vault/wiki/sources/Weakness Remediation Map Source.md").is_file(), "WLRM vault source missing")
check((ROOT / "vault/agent/session-logs/2026-08-27-6g-weakness-remediation.md").is_file(), "6G session log missing")

# Current/living stale markers must be gone.
living = core + [
    "docs/LOCAL_MANAGER_HANDOFF.md", "docs/GRANULAR_CAPABILITY_MAP_PLAN.md",
    "vault/agent/CURRENT_CONTEXT.md", "vault/agent/OPEN_LOOPS.md",
    "vault/wiki/sources/Execution State Source.md", "vault/wiki/projects/AI Infra Learning Coach Delivery.md",
]
stale_markers = [
    "6G henüz yürütülmedi",
    "6G active-not-executed",
    "6G weakness/remediation operationalization **AKTİF**",
    "6G 🟡 Weakness localization + remediation mapping",
    "6G — Weakness localization + remediation mapping`**\n**6G",
    "6A–6D TAMAMLANDI / 6E AKTİF",
    "6E — GPU / ML / Inference detailed map 🟡 AKTİF",
]
for path in living:
    t = text(path)
    for marker in stale_markers:
        check(marker not in t, f"{path}: stale current marker remains: {marker}")

# Repo-wide semantic stale scan. Historical logs are allowed to narrate past state.
ignore_historical_prefixes = ["docs/PROGRESS_LOG.md", "vault/agent/session-logs/"]
repo_stale_phrases = [
    "6G henüz yürütülmedi",
    "6G active-not-executed",
    "6A–6D TAMAMLANDI / 6E AKTİF",
]
for path in ROOT.rglob("*.md"):
    rel = path.relative_to(ROOT).as_posix()
    if any(rel == p or rel.startswith(p) for p in ignore_historical_prefixes):
        continue
    content = path.read_text(encoding="utf-8")
    for phrase in repo_stale_phrases:
        check(phrase not in content, f"repo-wide stale reference {phrase!r} in {rel}")

# 6G package QA invariants.
qa = yaml.safe_load((ROOT / "curriculum/decomposition/6g_weakness_remediation/qa_report.yaml").read_text(encoding="utf-8"))
check(qa["result"] == "PASS_WITH_OPEN_NON_BLOCKING_REVIEWS", "6G QA result unexpected")
check(qa["counts"]["skills_covered"] == 543, "6G Skill coverage mismatch")
check(qa["counts"]["objectives_covered"] == 590, "6G Objective coverage mismatch")
check(qa["counts"]["remediation_routes"] == 590, "6G route count mismatch")
check(qa["counts"]["open_blocking_reviews"] == 0, "6G blocking review exists")
check(qa["external_research_qa"]["status"] == "pending" and qa["external_research_qa"]["owner_step"] == "6H", "6H external QA handoff missing")

# 6H must remain not executed / external research mandatory.
for path in ["docs/STEP_STATUS.md", "docs/HANDOFF_STATE.md", "docs/START_HERE.md", "vault/agent/CURRENT_CONTEXT.md"]:
    t = text(path)
    check("6H" in t and ("henüz yürütülmedi" in t or "not" in t.lower()), f"{path}: 6H not-executed guard missing")
check("Independent external Research QA" in text("docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md") or "independent external Research QA" in text("docs/WEAKNESS_LOCALIZATION_REMEDIATION_MAP.md"), "WLRM 6H Research guard missing")

# Mechanical vault/external-memory validator.
result = subprocess.run([sys.executable, "tools/validate_external_memory.py"], cwd=ROOT)
check(result.returncode == 0, "validate_external_memory.py failed")

if failures:
    print("POST_6G_STALE_AUDIT=FAIL")
    for failure in failures:
        print("- " + failure)
    raise SystemExit(1)

print("POST_6G_STALE_AUDIT=PASS")
print("current_state=6G complete / 6H active-not-executed")
