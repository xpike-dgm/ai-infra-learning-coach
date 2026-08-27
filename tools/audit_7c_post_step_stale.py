from __future__ import annotations

from pathlib import Path
import re
import yaml

ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / "curriculum" / "english" / "7c_daily_component" / "stale_reference_audit.yaml"

# Current/living sources. Historical specs, research, prior finalizer scripts and old audit artifacts
# may legitimately describe 7C as future/active and are therefore not blocking sources.
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

# Keep each pattern scoped to the 7C clause itself so a line such as
# "7C ✅ ...; 7D 🟡 active-not-executed" is not a false positive.
STALE_7C_PATTERNS = [
    re.compile(r"7C\s*🟡(?:\s*(?:ACTIVE|AKTİF|active-not-executed))?", re.I),
    re.compile(r"7C\s*(?:—|:)\s*(?:Günlük English bileşeni\s*)?(?:—\s*)?(?:\*\*)?(?:ACTIVE|AKTİF|active-not-executed|HENÜZ YÜRÜTÜLMEDİ|henüz yürütülmedi)", re.I),
    re.compile(r"7C\s+(?:henüz yürütülmedi|HENÜZ YÜRÜTÜLMEDİ)", re.I),
    re.compile(r"(?:Aktif(?:\s+adım|\s*:)?)\s*[:*` ]*7C(?:\b|\s|—)", re.I),
    re.compile(r"7C[^\n;,]{0,80}(?:Fresh PRE|kullanıcı açık onayı)[^\n;,]{0,50}zorunlu", re.I),
]

# These patterns specifically protect LOCAL_MANAGER_HANDOFF from old contradictory takeover snapshots.
STALE_HANDOFF_PATTERNS = [
    re.compile(r"(?:Aktif adım|ACTIVE).{0,100}\b6F\b", re.I),
    re.compile(r"\b6F\b.{0,100}(?:ACTIVE|AKTİF|HENÜZ YÜRÜTÜLMEDİ|NOT EXECUTED)", re.I),
    re.compile(r"Son tamamlanan numaralı adım.{0,120}\b6E\b", re.I),
    re.compile(r"Sıradaki gerçek numbered work.{0,120}\b6[EF]\b", re.I),
]

REQUIRED_CURRENT_MARKERS = {
    "AGENTS.md": ["7C: ✅ `DECP-v0 / D-065`", "Aktif adım: 7D"],
    "PROJECT_CONTEXT.md": ["7C ✅ DECP-v0 / D-065", "7D 🟡 Teknik entegrasyon"],
    "docs/START_HERE.md": ["7C ✅ DECP-v0 / D-065", "7D 🟡 active-not-executed"],
    "docs/HANDOFF_STATE.md": ["7C — DECP-v0 / D-065", "Aktif:** `7D — Teknik entegrasyon`"],
    "docs/STEP_STATUS.md": ["7C — Günlük English bileşeni** | ✅", "7D — Teknik entegrasyon** | 🟡 Aktif"],
    "docs/EXECUTION_INDEX.md": ["[x] **7C — Günlük English bileşeni** — `DECP-v0 / D-065`", "7D — Teknik entegrasyon** **AKTİF"],
    "docs/MASTER_PLAN.md": ["[x] 7C — Günlük English bileşeni — DECP-v0 / D-065", "7D — Teknik entegrasyon — **AKTİF**"],
    "docs/LOCAL_MANAGER_HANDOFF.md": ["7C ✅ DECP-v0 / D-065", "7D 🟡 ACTIVE — NOT EXECUTED"],
    "vault/agent/CURRENT_CONTEXT.md": ["7C — DECP-v0 / D-065", "7D — Teknik entegrasyon"],
    "vault/agent/OPEN_LOOPS.md": ["7C Daily English component: DECP-v0 / D-065", "7D Technical integration **AKTİF**"],
}


def excerpt(text: str, start: int, end: int) -> str:
    lo = max(0, start - 60)
    hi = min(len(text), end + 100)
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
        file_findings = []
        for pattern in STALE_7C_PATTERNS:
            for match in pattern.finditer(text):
                file_findings.append({
                    "pattern": pattern.pattern,
                    "match": excerpt(text, match.start(), match.end()),
                })
        if rel == "docs/LOCAL_MANAGER_HANDOFF.md":
            for pattern in STALE_HANDOFF_PATTERNS:
                for match in pattern.finditer(text):
                    file_findings.append({
                        "pattern": pattern.pattern,
                        "match": excerpt(text, match.start(), match.end()),
                    })
        if file_findings:
            for finding in file_findings:
                blocking.append({"file": rel, **finding})

        missing_markers = [marker for marker in REQUIRED_CURRENT_MARKERS.get(rel, []) if marker not in text]
        if missing_markers:
            blocking.append({"file": rel, "reason": "required_current_marker_missing", "markers": missing_markers})
        checked.append({"file": rel, "stale_matches": len(file_findings), "missing_markers": missing_markers})

    result = "PASS" if not blocking else "FAIL"
    report = {
        "stage_step": "7C",
        "model": "DECP-v0",
        "decision": "D-065",
        "audit": "repo_wide_stale_reference_post_step",
        "result": result,
        "checked_current_files": checked,
        "blocking_findings": blocking,
        "historical_scope_policy": "prior finalizer/audit/research/spec history is non-blocking; living current sources must agree on 7C complete / 7D active-not-executed",
    }
    REPORT.parent.mkdir(parents=True, exist_ok=True)
    REPORT.write_text(yaml.safe_dump(report, sort_keys=False, allow_unicode=True, width=160), encoding="utf-8")

    print(f"7C_POST_STALE_AUDIT={result}")
    print(f"checked_current_files={len(checked)} blocking={len(blocking)}")
    if blocking:
        for finding in blocking:
            print("-", finding)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
