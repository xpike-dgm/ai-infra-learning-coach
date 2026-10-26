"""Ships the course's code-test suites and the PC runner with the app (16G, `HLPX-v0`).

The learner runs a code task's tests on their own computer (14D, `CDEX-v0`): they copy the runner and the task's suite
from the app, run the runner against their code, and paste the report back (user decision, 2026-10-26: copy-paste, no
network or file permission). So the app carries, byte for byte as authored:

- every `code_test_suite/1` JSON under `curriculum/content/*/suites/`, named by the item version it is pinned to
  (`code_suites/<item logical id>-v<N>.json`), and
- `tools/code_test_runner.py` as `code_test_runner.py`.

Nothing is rewritten: a shipped file is the authored file. `--check` verifies the shipped set equals the sources (no file
missing, none extra, every byte the same) and exits non-zero otherwise.

    python tools/ship_code_suites.py [--check]
"""

from __future__ import annotations

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "android/app-wiring/src/main/assets"
SUITES = ASSETS / "code_suites"
RUNNER = ROOT / "tools/code_test_runner.py"


def asset_name(item: str) -> str:
    logical, version = item.rsplit("@v", 1)
    return f"{logical}-v{int(version)}.json"


def sources() -> dict[str, bytes]:
    shipped: dict[str, bytes] = {}
    for path in sorted((ROOT / "curriculum/content").glob("*/suites/*.json")):
        raw = path.read_bytes()
        document = json.loads(raw.decode("utf-8"))
        if document.get("format") != "code_test_suite/1":
            raise SystemExit(f"{path}: not a code_test_suite/1 document")
        name = asset_name(document["item"])
        if name in shipped:
            raise SystemExit(f"{path}: a second suite for {document['item']}")
        shipped[name] = raw
    return shipped


def main() -> int:
    wanted = sources()
    if "--check" in sys.argv:
        present = {p.name: p.read_bytes() for p in SUITES.glob("*.json")} if SUITES.is_dir() else {}
        missing = sorted(set(wanted) - set(present))
        extra = sorted(set(present) - set(wanted))
        changed = sorted(n for n in set(wanted) & set(present) if wanted[n] != present[n])
        runner_ok = (ASSETS / "code_test_runner.py").is_file() and (ASSETS / "code_test_runner.py").read_bytes() == RUNNER.read_bytes()
        print(f"suites={len(wanted)} missing={len(missing)} extra={len(extra)} changed={len(changed)} runner={'same' if runner_ok else 'DIFFERENT'}")
        return 0 if not (missing or extra or changed) and runner_ok else 1
    SUITES.mkdir(parents=True, exist_ok=True)
    for stale in SUITES.glob("*.json"):
        if stale.name not in wanted:
            stale.unlink()
    for name, raw in wanted.items():
        (SUITES / name).write_bytes(raw)
    (ASSETS / "code_test_runner.py").write_bytes(RUNNER.read_bytes())
    print(f"shipped {len(wanted)} suites and the runner")
    return 0


if __name__ == "__main__":
    sys.exit(main())
