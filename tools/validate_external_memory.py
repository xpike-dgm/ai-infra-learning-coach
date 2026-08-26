"""Validate the repository's durable external-memory navigation layer.

Usage: python tools/validate_external_memory.py
"""

from __future__ import annotations

import re
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parent.parent
VAULT = ROOT / "vault"
REQUIRED = (
    "AGENTS.md",
    "vault/agent/SESSION_START.md",
    "vault/agent/CURRENT_CONTEXT.md",
    "vault/agent/OPEN_LOOPS.md",
    "vault/agent/NEW_CHAT_PROMPT.md",
    "vault/agent/checklists/MEMORY_HEALTH_CHECKLIST.md",
    "vault/templates/Session Handoff.md",
    "vault/wiki/mocs/Repository Document Map.md",
    "docs/PROJECT_MEMORY_PROTOCOL.md",
    "docs/EXECUTION_INDEX.md",
    "docs/STEP_STATUS.md",
    "docs/HANDOFF_STATE.md",
    "PROJECT_CONTEXT.md",
    "docs/MASTER_PLAN.md",
    "docs/DECISIONS.md",
)
LINK = re.compile(r"\[\[([^\]|#]+)")


def exists_for_wikilink(target: str) -> bool:
    candidate = ROOT / target
    return candidate.exists() or candidate.with_suffix(".md").exists()


def main() -> int:
    errors: list[str] = []
    for relative_path in REQUIRED:
        if not (ROOT / relative_path).is_file():
            errors.append(f"missing required memory file: {relative_path}")

    broken_links: list[str] = []
    for note in VAULT.rglob("*.md"):
        for match in LINK.finditer(note.read_text(encoding="utf-8")):
            target = match.group(1).strip()
            if not exists_for_wikilink(target):
                broken_links.append(f"{note.relative_to(ROOT)} -> {target}")

    if broken_links:
        errors.extend(f"broken wikilink: {item}" for item in sorted(set(broken_links)))

    session_start = (ROOT / "vault/agent/SESSION_START.md")
    if session_start.is_file():
        text = session_start.read_text(encoding="utf-8")
        for authority in (
            "AGENTS",
            "PROJECT_MEMORY_PROTOCOL",
            "EXECUTION_INDEX",
            "STEP_STATUS",
            "HANDOFF_STATE",
            "PROJECT_CONTEXT",
            "MASTER_PLAN",
        ):
            if authority not in text:
                errors.append(f"SESSION_START missing authority pointer: {authority}")

    if errors:
        print("EXTERNAL_MEMORY_QA=FAIL")
        print(*errors, sep="\n")
        return 1

    note_count = sum(1 for _ in VAULT.rglob("*.md"))
    link_count = sum(
        len(LINK.findall(note.read_text(encoding="utf-8")))
        for note in VAULT.rglob("*.md")
    )
    print("EXTERNAL_MEMORY_QA=PASS")
    print(f"required_files={len(REQUIRED)}/{len(REQUIRED)}")
    print(f"vault_notes={note_count}")
    print(f"vault_wikilinks={link_count}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
