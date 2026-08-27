from __future__ import annotations

from pathlib import Path
import re
import yaml

ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / "curriculum/english/7d_technical_integration/stale_reference_audit.yaml"

CURRENT_FILES = [
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

# Current/living sources must no longer claim that 7D is the active/not-executed step.
STALE_PATTERNS = [
    re.compile(r"\b7D\b\s*(?:🟡|—|:)?[^\n,;.!?]{0,100}(?:active-not-executed|ACTIVE\s*—\s*NOT EXECUTED|henüz yürütülmedi|HENÜZ YÜRÜTÜLMEDİ)", re.I),
    re.compile(r"(?:\bAktif(?:\s+adım)?\b|\bACTIVE\b)[^\n,;.!?]{0,100}\b7D\b", re.I),
    re.compile(r"\b7D\b\s*🟡", re.I),
]

REQUIRED_MARKERS = {
    "AGENTS.md": ["7D: ✅ `TEIP-v0 / D-066`", "Aktif adım: 7E"],
    "PROJECT_CONTEXT.md": ["7D ✅ TEIP-v0 / D-066", "7E 🟡 English mastery"],
    "docs/START_HERE.md": ["7D ✅ TEIP-v0 / D-066", "7E 🟡 active-not-executed"],
    "docs/HANDOFF_STATE.md": ["D-066 / 7D final özeti", "7E handoff"],
    "docs/STEP_STATUS.md": ["7D — Teknik entegrasyon** | ✅", "7E — English mastery** | 🟡 Aktif"],
    "docs/EXECUTION_INDEX.md": ["[x] **7D — Teknik entegrasyon** — `TEIP-v0 / D-066`", "7E — English mastery** **AKTİF**"],
    "docs/MASTER_PLAN.md": ["[x] 7D — Teknik entegrasyon — TEIP-v0 / D-066", "7E — English mastery — **AKTİF**"],
    "docs/LOCAL_MANAGER_HANDOFF.md": ["7D completion addendum — D-066", "Current active numbered step 7E"],
    "vault/agent/CURRENT_CONTEXT.md": ["TEIP-v0 / D-066", "Aktif adım **7E — English mastery**"],
    "vault/agent/OPEN_LOOPS.md": ["7D Technical integration: TEIP-v0 / D-066", "7E English mastery **AKTİF**"],
}


def excerpt(text: str, start: int, end: int) -> str:
    lo = max(0, start - 70)
    hi = min(len(text), end + 120)
    return re.sub(r"\s+", " ", text[lo:hi]).strip()


def main() -> int:
    blocking: list[dict] = []
    checked: list[dict] = []
    for rel in CURRENT_FILES:
        path = ROOT / rel
        if not path.is_file():
            blocking.append({"file": rel, "reason": "missing_current_file"})
            continue
        text = path.read_text(encoding="utf-8")
        findings = []
        for pattern in STALE_PATTERNS:
            for match in pattern.finditer(text):
                findings.append({"pattern": pattern.pattern, "match": excerpt(text, match.start(), match.end())})
        missing = [m for m in REQUIRED_MARKERS[rel] if m not in text]
        for finding in findings:
            blocking.append({"file": rel, **finding})
        if missing:
            blocking.append({"file": rel, "reason": "required_current_marker_missing", "markers": missing})
        checked.append({"file": rel, "stale_matches": len(findings), "missing_markers": missing})

    result = "PASS" if not blocking else "FAIL"
    report = {
        "stage_step": "7D",
        "model": "TEIP-v0",
        "decision": "D-066",
        "audit": "repo_wide_stale_reference_post_step",
        "result": result,
        "checked_current_files": checked,
        "blocking_findings": blocking,
        "historical_scope_policy": "historical specs/research/prior finalizers are non-blocking; current/living sources must agree on 7D complete and 7E active-not-executed",
    }
    REPORT.parent.mkdir(parents=True, exist_ok=True)
    REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True, width=160), encoding="utf-8")
    print(f"7D_POST_STALE_AUDIT={result}")
    print(f"checked={len(checked)} blocking={len(blocking)}")
    for finding in blocking:
        print("-", finding)
    return 0 if result == "PASS" else 1


if __name__ == "__main__":
    raise SystemExit(main())
