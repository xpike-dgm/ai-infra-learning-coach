from __future__ import annotations

from pathlib import Path
import re
import sys
import yaml

ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / "curriculum/english/7e_mastery_profile/stale_reference_audit.yaml"

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

STALE_PATTERNS = [
    re.compile(r"\b7E\b[^\n]{0,100}(?:🟡|active-not-executed|AKTİF[^\n]{0,30}(?:HENÜZ|henüz)|henüz yürütülmedi|HENÜZ YÜRÜTÜLMEDİ)", re.I),
    re.compile(r"(?:Aktif adım|Aktif step|Sıradaki numaralı çalışma|Sıradaki gerçek numbered work)[^\n]{0,80}\b7E\b", re.I),
    re.compile(r"\b7E\s*[–-]\s*20\b"),
]

REQUIRED_MARKERS = {
    "AGENTS.md": ["7E: ✅ `TEPM-v0 / D-067`", "Aktif adım: 8A"],
    "PROJECT_CONTEXT.md": ["7E ✅ TEPM-v0 / D-067", "8A 🟡 Bilgi mimarisi"],
    "docs/START_HERE.md": ["D-067 — TEPM-v0", "8A 🟡 active-not-executed"],
    "docs/HANDOFF_STATE.md": ["D-067 / 7E final özeti", "8A handoff"],
    "docs/STEP_STATUS.md": ["7E — English mastery** | ✅", "8A — Bilgi mimarisi** | 🟡 Aktif"],
    "docs/EXECUTION_INDEX.md": ["[x] **7E — English mastery** — `TEPM-v0 / D-067`", "[ ] **8A — Bilgi mimarisi** **AKTİF**"],
    "docs/MASTER_PLAN.md": ["[x] 7E — English mastery — TEPM-v0 / D-067", "[ ] 8A — Bilgi mimarisi — **AKTİF**"],
    "docs/LOCAL_MANAGER_HANDOFF.md": ["7E completion addendum — D-067", "Aktif adım:** `8A — Bilgi mimarisi`"],
    "vault/agent/CURRENT_CONTEXT.md": ["7E — TEPM-v0 / D-067", "Aktif adım **8A — Bilgi mimarisi**"],
    "vault/agent/OPEN_LOOPS.md": ["[x] 7E English mastery", "[ ] 8A Bilgi mimarisi **AKTİF**"],
}


def main() -> int:
    checked = []
    blocking = []

    for rel in CURRENT_FILES:
        path = ROOT / rel
        if not path.exists():
            blocking.append({"file": rel, "reason": "missing_current_file"})
            continue
        text = path.read_text(encoding="utf-8")
        matches = []
        for pat in STALE_PATTERNS:
            for m in pat.finditer(text):
                matches.append({"pattern": pat.pattern, "match": m.group(0)[:240]})
        missing = [m for m in REQUIRED_MARKERS.get(rel, []) if m not in text]
        checked.append({"file": rel, "stale_matches": len(matches), "missing_markers": missing})
        for match in matches:
            blocking.append({"file": rel, **match})
        if missing:
            blocking.append({"file": rel, "reason": "required_current_marker_missing", "markers": missing})

    # Repo-wide scan is recorded for transparency. Historical specs/research/finalizers may
    # correctly mention that 7E was future/active at the time they were authored; only living
    # current-state sources above are blocking.
    historical_hits = []
    scanned_files = 0
    current_set = set(CURRENT_FILES)
    for path in ROOT.rglob("*"):
        if not path.is_file() or ".git" in path.parts:
            continue
        if path.suffix.lower() not in {".md", ".yaml", ".yml", ".py"}:
            continue
        rel = path.relative_to(ROOT).as_posix()
        scanned_files += 1
        if rel in current_set:
            continue
        try:
            text = path.read_text(encoding="utf-8")
        except UnicodeDecodeError:
            continue
        for pat in STALE_PATTERNS:
            m = pat.search(text)
            if m:
                historical_hits.append({"file": rel, "sample": m.group(0)[:200], "classification": "historical_non_blocking"})
                break

    report = {
        "stage_step": "7E",
        "model": "TEPM-v0",
        "decision": "D-067",
        "audit": "repo_wide_stale_reference_post_step",
        "result": "PASS" if not blocking else "FAIL",
        "repo_files_scanned": scanned_files,
        "checked_current_files": checked,
        "historical_non_blocking_hits": historical_hits,
        "blocking_findings": blocking,
        "historical_scope_policy": "historical specs/research/prior finalizers may preserve at-the-time future-state language; current/living sources must agree on 7E complete, Stage 7 complete, and 8A active-not-executed",
    }
    REPORT.parent.mkdir(parents=True, exist_ok=True)
    REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True, width=160), encoding="utf-8")

    print(f"7E_POST_STALE_AUDIT={'PASS' if not blocking else 'FAIL'}")
    print(f"repo_files_scanned={scanned_files} current_checked={len(checked)} blocking={len(blocking)} historical_non_blocking={len(historical_hits)}")
    if blocking:
        for item in blocking:
            print("-", item)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
