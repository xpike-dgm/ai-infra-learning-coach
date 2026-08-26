from pathlib import Path
import subprocess
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
failures = []

def check(cond, msg):
    if not cond: failures.append(msg)

def text(path):
    return (ROOT / path).read_text(encoding="utf-8")

core = ["docs/EXECUTION_INDEX.md", "docs/STEP_STATUS.md", "docs/HANDOFF_STATE.md", "PROJECT_CONTEXT.md", "docs/MASTER_PLAN.md"]
for path in core:
    t = text(path)
    check("6F" in t and ("PEM-v0" in t or "D-060" in t), f"{path}: 6F completion missing")
    check("6G" in t, f"{path}: 6G current step missing")

living = core + [
    "AGENTS.md", "docs/START_HERE.md", "docs/LOCAL_MANAGER_HANDOFF.md",
    "vault/agent/CURRENT_CONTEXT.md", "vault/agent/OPEN_LOOPS.md",
    "vault/wiki/sources/Execution State Source.md", "vault/wiki/projects/AI Infra Learning Coach Delivery.md",
]
stale_markers = [
    "6F henüz yürütülmedi", "6F — Professional engineering / project map`**\n**6F",
    "6F 🟡 Professional engineering / project map", "6F active-not-executed",
    "6F Professional Engineering detailed map **AKTİF**",
]
for path in living:
    t = text(path)
    for marker in stale_markers:
        check(marker not in t, f"{path}: stale current marker remains: {marker}")

check("## D-060 — Professional engineering / projects detailed map = PEM-v0" in text("docs/DECISIONS.md"), "D-060 decision missing")
check((ROOT / "docs/PROFESSIONAL_ENGINEERING_DETAILED_MAP.md").is_file(), "PEM canonical summary missing")
check((ROOT / "vault/wiki/sources/Professional Engineering Map Source.md").is_file(), "PEM vault source note missing")

sdm_reviews = yaml.safe_load((ROOT / "curriculum/decomposition/6d_systems/review_queue.yaml").read_text(encoding="utf-8"))
gim_reviews = yaml.safe_load((ROOT / "curriculum/decomposition/6e_gpu_ml_inference/review_queue.yaml").read_text(encoding="utf-8"))
check(next(x for x in sdm_reviews if x["review_id"] == "review.6d.professional_overlay_reconciliation")["status"] == "resolved", "6D professional overlay review still open")
check(next(x for x in gim_reviews if x["review_id"] == "review.6e.professional_overlay_reconciliation")["status"] == "resolved", "6E professional overlay review still open")
check(sum(x["status"] == "open" for x in sdm_reviews) == 2, "6D open review count should be 2")
check(sum(x["status"] == "open" for x in gim_reviews) == 3, "6E open review count should be 3")
check("| Açık non-blocking review | 2 |" in text("docs/SYSTEMS_DETAILED_MAP.md"), "Systems summary stale review count")
check("- Open reviews: 0 blocking / 3 non-blocking" in text("docs/GPU_ML_INFERENCE_DETAILED_MAP.md"), "GIM summary stale review count")

qa = yaml.safe_load((ROOT / "curriculum/decomposition/6f_professional_engineering/qa_report.yaml").read_text(encoding="utf-8"))
check(qa["result"] == "PASS_WITH_OPEN_NON_BLOCKING_REVIEWS", "6F QA result unexpected")
check(qa["counts"]["open_blocking_reviews"] == 0, "6F blocking review exists")
check(qa["external_research_qa"]["status"] == "pending" and qa["external_research_qa"]["owner_step"] == "6H", "6H external QA handoff missing")

# External-memory mechanical validator must still pass after vault edits.
result = subprocess.run([sys.executable, "tools/validate_external_memory.py"], cwd=ROOT)
check(result.returncode == 0, "validate_external_memory.py failed")

if failures:
    print("POST_6F_STALE_AUDIT=FAIL")
    for failure in failures: print("- " + failure)
    raise SystemExit(1)
print("POST_6F_STALE_AUDIT=PASS")
print("current_state=6F complete / 6G active-not-executed")
